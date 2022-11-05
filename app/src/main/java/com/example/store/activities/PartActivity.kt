package com.example.store.activitiesimport android.content.SharedPreferencesimport android.os.Bundleimport android.support.v7.app.AlertDialogimport android.text.TextUtilsimport android.view.MenuItemimport android.view.Viewimport android.widget.EditTextimport android.widget.ImageViewimport com.example.store.Rimport com.example.store.commoners.AppUtilsimport com.example.store.commoners.BaseActivityimport com.example.store.commoners.Kimport com.example.store.models.Partimport com.example.store.models.PartOrderimport com.example.store.utils.*import com.mikepenz.fontawesome_typeface_library.FontAwesomeimport com.mikepenz.ionicons_typeface_library.Ioniconsimport com.synnapps.carouselview.ImageListenerimport kotlinx.android.synthetic.main.activity_part.*import kotlinx.android.synthetic.main.make_order.*import com.example.store.utils.PreferenceHelper.getimport com.google.gson.Gsonimport com.google.gson.JsonObjectimport okhttp3.*import org.jetbrains.anko.alertimport org.jetbrains.anko.toastimport timber.log.Timberimport java.io.IOExceptionclass PartActivity : BaseActivity(), ImageListener, View.OnClickListener {    private lateinit var part: Part    private lateinit var prefs: SharedPreferences    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        setContentView(R.layout.activity_part)        prefs = PreferenceHelper.defaultPrefs(this)        part = intent.getSerializableExtra(K.PART) as Part        initViews()        loadPart()    }    private fun initViews() {        setSupportActionBar(toolbar)        supportActionBar?.setDisplayHomeAsUpEnabled(true)        supportActionBar?.setDisplayShowHomeEnabled(true)        supportActionBar?.title = part.name        if (part.sellerId == getUid()) isMyPart() else notMyPart()        carousel.pageCount = part.images.size        carousel.setImageListener(this)        seller_icon.setImageDrawable(            AppUtils.setDrawable(                this,                FontAwesome.Icon.faw_user2,                R.color.secondaryText,                15            )        )        location_icon.setImageDrawable(            AppUtils.setDrawable(                this,                Ionicons.Icon.ion_location,                R.color.secondaryText,                15            )        )        category_icon.setImageDrawable(            AppUtils.setDrawable(                this,                FontAwesome.Icon.faw_list,                R.color.secondaryText,                15            )        )        order.setOnClickListener(this)        delete.setOnClickListener(this)    }    override fun setImageForPosition(position: Int, imageView: ImageView?) {        val keys = part.images.keys.toList()        imageView!!.scaleType = ImageView.ScaleType.CENTER_CROP        imageView.loadUrl(part.images[keys[position]]!!)    }    private fun loadPart() {        seller.text = part.sellerName        location.text = part.location        category.text = part.category        qty.text = part.quantity.toString()        unit.text = "${part.price}"    }    private fun isMyPart() {        order.hideView()        //contactSeller.hideView()        delete.showView()    }    private fun notMyPart() {        delete.hideView()        //contactSeller.showView()        order.showView()    }    override fun onClick(v: View?) {        when (v?.id) {            order.id -> {                val view = layoutInflater.inflate(R.layout.make_order, null, false)                val quantity = view.findViewById<EditText>(R.id.orderQuantity)                val location = view.findViewById<EditText>(R.id.orderLocation)                AlertDialog.Builder(this).setTitle("Make order").setView(view)                    .setPositiveButton("ORDER") { x, y ->                        if (!AppUtils.validated(quantity, location)) {                            //return@setPositiveButton                        }                        makeOrder(quantity.text.toString().toInt(), location.text.toString())                    }.setNegativeButton("CANCEL", null).create().show()            }            delete.id -> {                alert("Delete ${part.name}") {                    positiveButton("DELETE") {                        getFirestore().collection(K.PARTS).document(part.id!!).delete()                            .addOnSuccessListener {                                toast("${part.name} deleted")                                finish()                                AppUtils.animateEnterLeft(this@PartActivity)                            }                    }                    negativeButton("CANCEL") {}                }.show()            }        }    }    private fun makeOrder(quantity: Int, location: String) {        if (quantity < 1 || TextUtils.isEmpty(location)) {            toast("Please fill all fields")            return        }        val ref = getFirestore().collection(K.PARTS).document(part.id.toString())        ref.get().addOnSuccessListener { doc ->            if (doc == null) {                alert("Product does not exist", "Failed").show()            } else {                val snapshot = doc.toObject(Part::class.java)                var available = snapshot?.quantity                if (quantity > available!!) {                    alert("Quantity exceeds available amount", "Failed").show()                } else {                    available -= quantity                    val order = PartOrder()                    val buyerId = getUid()                    var now = System.currentTimeMillis().toString()                    order.date = now                    order.id = buyerId + part.id                    order.name = part.name                    order.buyerId = buyerId                    order.buyerName = prefs[K.NAME]                    order.sellerId = part.sellerId                    order.sellerName = part.sellerName                    order.image = part.image                    order.description = quantity.toString() + " " + part.name;                    getDatabaseReference().child(K.ORDERS).child(order.sellerId.toString())                        .child(part.id.toString()).setValue(order)                    getDatabaseReference().child(K.REQUESTS).child(order.buyerId.toString())                        .child(order.id.toString()).setValue(order)                    ref.update(mapOf("quantity" to available))                    alert("Order created succesfully", "Success!").show()                }            }        }.addOnFailureListener {            hideLoading()            toast("Error uploading order. Please try again")        }    }    override fun onOptionsItemSelected(item: MenuItem?): Boolean {        when (item?.itemId) {            android.R.id.home -> onBackPressed()        }        return true    }    override fun onBackPressed() {        super.onBackPressed()        AppUtils.animateEnterLeft(this)    }}