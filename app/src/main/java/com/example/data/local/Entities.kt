package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val productId: String,
    val quantity: Int,
    val swatchName: String,
    val swatchHex: Long,
    val assemblyBooked: Boolean,
    val destinationAddress: String
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey val productId: String,
    val folderName: String = "Main Wishlist",
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_blueprints")
data class SavedBlueprintEntity(
    @PrimaryKey val id: String,
    val name: String,
    val roomType: String,
    val widthMeters: Float,
    val lengthMeters: Float,
    val wallColorHex: Long,
    val flooringType: String,
    val itemsJson: String
)

@Entity(tableName = "user_orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val productIdsJson: String,
    val totalAmount: Double,
    val orderDate: String,
    val status: String,
    val isExpress: Boolean,
    val timeSlot: String,
    val address: String
)

@Entity(tableName = "warranty_records")
data class WarrantyEntity(
    @PrimaryKey val serialNumber: String,
    val productName: String,
    val purchaseDate: String,
    val warrantyYears: Int,
    val status: String
)
