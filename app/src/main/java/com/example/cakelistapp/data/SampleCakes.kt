package com.example.cakelistapp.data

import com.example.cakelistapp.domain.model.Cake

object SampleCakes {
    private val lemonCheesecake = Cake(
        title = "Lemon Cheesecake",
        description = "A cheesecake made of lemon",
        imageUrl = "https://s3-eu-west-1.amazonaws.com/s3.mediafileserver.co.uk/" +
            "carnation/WebFiles/RecipeImages/lemoncheesecake_lg.jpg",
    )
    private val bananaCake = Cake(
        title = "Banana Cake",
        description = "A banana cake",
        imageUrl = "https://hips.hearstapps.com/vidthumb/images/delish-banana-cake-4-1535137229.jpg",
    )
    private val dundeeCake = Cake(
        title = "Dundee Cake",
        description = "A staple with Dundonians",
        imageUrl = "https://img.delicious.com.au/xRc0uydo/del/2015/10/dundee-cake-10249-1.jpg",
    )

    val cakes: List<Cake> = listOf(
        lemonCheesecake,
        bananaCake,
        dundeeCake,
    )
}
