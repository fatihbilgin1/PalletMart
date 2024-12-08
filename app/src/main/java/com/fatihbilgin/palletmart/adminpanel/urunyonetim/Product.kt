package com.fatihbilgin.palletmart.adminpanel.urunyonetim

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: String? = null,         // Varsayılan değer ekledik
    val name: String = "",         // Varsayılan değer ekledik
    val description: String = "",  // Varsayılan değer ekledik
    val price: Double? = null,     // Varsayılan değer ekledik
    val stock: Int? = null,        // Varsayılan değer ekledik
    val imageUrl: String = "",      // Varsayılan değer ekledik
    var quantity: Int = 1 ,
    val sellerId: String = "",      // Satıcıyı tanımlayan sellerId ekledik
) : Parcelable {

    // Parametresiz kurucu, Firebase için gereklidir
    constructor() : this(null, "", "", null, null, "",1,"")

    constructor(parcel: Parcel) : this(
        parcel.readString(),         // id
        parcel.readString() ?: "",   // name
        parcel.readString() ?: "",   // description
        parcel.readDouble(),         // price
        parcel.readInt(),            // stock
        parcel.readString() ?: "" ,   // imageUrl
        parcel.readInt(),                    //quantity
        parcel.readString()?: ""  // sellerId
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)            // id
        parcel.writeString(name)          // name
        parcel.writeString(description)   // description
        parcel.writeDouble(price ?: 0.0)  // price
        parcel.writeInt(stock ?: 0)       // stock
        parcel.writeString(imageUrl)      // imageUrl
        parcel.writeInt(quantity)        // quantity
        parcel.writeString(sellerId)     //sellerId
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}