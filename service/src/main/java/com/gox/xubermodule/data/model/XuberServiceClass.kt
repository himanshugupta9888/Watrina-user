package com.gox.xubermodule.data.model

object XuberServiceClass {
    var serviceName: String = ""
    var serviceID: Int = 0
    var mainServiceName: String = ""
    var mainServiceID: Int = 0
    var subServiceName: String = ""
    var subServiceID: Int = 0
    var quantity: String = "0"
    var baseFare: String = ""
    var fareType: String = ""
    var allowQuantity: String = ""
    var allowDesc: String = ""
    var time: String = ""
    var date: String = ""
    var sourceLat: String = ""
    var sourceLng: String = ""
    var address: String = ""
    var picture: String = ""
    var providerListModel: ProviderListModel.ResponseData.ProviderService = ProviderListModel.ResponseData.ProviderService()
}
