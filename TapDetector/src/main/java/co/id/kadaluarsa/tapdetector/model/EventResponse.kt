package co.id.kadaluarsa.tapdetector.model


data class EventResponse(
    val results: List<ResultsItem>
)

data class ResultsItem(
    val createdAt: String,
    val UserId: String,
    val TapPositionX: Double,
    val Latitude: Double,
    val TapPositionY: Double,
    val Longitude: Double,
    val objectId: String,
    val updatedAt: String
) {
    override fun toString(): String {
        return "createAt : $createdAt\n" +
                "UserId  : $UserId\n" +
                "tapPositionX : $TapPositionX\n" +
                "tapPositionY : $TapPositionY\n" +
                "latitude : $Latitude\n" +
                "longitude : $Longitude\n" +
                "objectId : $objectId\n" +
                "updatedAt : $updatedAt"
    }
}
