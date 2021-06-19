package com.gox.app.data.repositary.remote.model

data class HistoryModel(
        val error: List<Any> = listOf(),
        val message: String = "",
        val responseData: ResponseData = ResponseData(),
        val statusCode: String = "",
        val title: String = ""
) {
    data class ResponseData(
            val total_records: String = "",
            val transport: ArrayList<Transport> = ArrayList(),
            val service: ArrayList<Service> = ArrayList(),
            val order: ArrayList<Order> = ArrayList(),
            val type: String = ""
    ) {
        data class Transport(
                val assigned_at: String = "",
                val assigned_time: String = "",
                val booking_id: String = "",
                val d_address: String = "",
                val finished_time: String = "",
                val id: String = "",
                val payment: Payment = Payment(),
                val provider: Provider = Provider(),
                val provider_id: String = "",
                val provider_vehicle: ProviderVehicle = ProviderVehicle(),
                val provider_vehicle_id: String = "",
                val ride: Ride = Ride(),
                val ride_delivery_id: String = "",
                val s_address: String = "",
                val schedule_time: String = "",
                val started_time: String = "",
                val static_map: String = "",
                val status: String = "",
                val timezone: String = "",
                val user: User = User(),
                val user_id: String = ""
        ) {
            data class User(
                    val first_name: String = "",
                    val id: String = "",
                    val last_name: String = "",
                    val picture: String = "",
                    val rating: String = ""
            )

            data class Payment(
                    val ride_request_id: String = "",
                    val total: String = " "
            )

            data class ProviderVehicle(
                    val id: String = "",
                    val provider_id: String = "",
                    val vehicle_make: String = "",
                    val vehicle_model: String = "",
                    val vehicle_no: String = ""
            )

            data class Ride(
                    val id: String = "",
                    val vehicle_image: String = "",
                    val vehicle_name: String = ""
            )

            data class Provider(
                    val first_name: String = "",
                    val id: String = "",
                    val last_name: String = "",
                    val picture: String = "",
                    val rating: String = " "
            )
        }


        data class Service(
                val assigned_at: String = "",
                val booking_id: String = "",
                val company_id: String = "",
                val id: String = "",
                val payment: Payment = Payment(),
                val provider_id: String = "",
                val s_address: String = "",
                val service: Service = Service(),
                val service_id: String = "",
                val started_at: String = "",
                val static_map: String = "",
                val status: String = "",
                val user: User = User(),
                val user_id: String = ""
        ) {
            data class User(
                    val first_name: String = "",
                    val id: String = "",
                    val last_name: String = "",
                    val rating: String = ""
            )

            data class Service(
                    val id: String = "",
                    val service_category_id: String = "",
                    val service_name: String = ""
            )

            data class Payment(
                    val id: String = "",
                    val service_request_id: String = "",
                    val total: String = " "
            )
        }


        data class Order(
                val admin_service_id: String = "",
                val company_id: String = "",
                val created_at: String = "",
                val cuisines: String = "",
                val delivery: Delivery = Delivery(),
                val delivery_address: String = "",
                val id: String = "",
                val pickup: Pickup = Pickup(),
                val pickup_address: String = "",
                val provider_id: String = "",
                val rating: Rating = Rating(),
                val static_map: String = "",
                val status: String = "",
                val store_id: String = "",
                val store_order_invoice_id: String = "",
                val total: String = "",
                val user_id: String = ""
        ) {
            data class Delivery(
                    val flat_no: String = "",
                    val id: String = "",
                    val latitude: String = " ",
                    val longitude: String = " ",
                    val map_address: Any = Any(),
                    val street: String = ""
            )

            data class Pickup(
                    val contact_number: String = "",
                    val id: String = "",
                    val latitude: String = " ",
                    val longitude: String = " ",
                    val picture: String = "",
                    val store_location: String = "",
                    val store_name: String = "",
                    val store_type_id: String = "",
                    val storetype: Storetype = Storetype()
            ) {
                data class Storetype(
                        val category: String = "",
                        val company_id: String = "",
                        val id: String = "",
                        val name: String = "",
                        val status: String = ""
                )
            }

            data class Rating(
                    val id: String = "",
                    val provider_comment: String = "",
                    val provider_rating: String = "",
                    val store_rating: String = " ",
                    val user_comment: String = "",
                    val user_rating: String = " "
            )
        }
    }
}
