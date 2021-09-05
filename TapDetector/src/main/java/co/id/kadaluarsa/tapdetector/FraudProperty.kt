package co.id.kadaluarsa.tapdetector

class FraudProperty {
    private var map = hashMapOf<String, Any>()

    fun put(property: Pair<String, Any>) = apply {
        map[property.first] = property.second
    }

}