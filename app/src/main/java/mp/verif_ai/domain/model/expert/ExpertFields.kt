package mp.verif_ai.domain.model.expert

data object ExpertFields {
   // Education/Academics
    data object Education {
        const val ELEMENTARY = "Elementary Education"
        const val SECONDARY = "Secondary Education"
        const val COLLEGE = "Higher Education"
        const val LANGUAGE = "Languages"
        const val STUDY_ABROAD = "Study Abroad"
    }

    // Computer/Internet
    data object IT {
        const val PROGRAMMING = "Programming"
        const val HARDWARE = "Computer Hardware"
        const val MOBILE = "Mobile/Apps"
        const val SECURITY = "Security/Hacking"
        const val DIGITAL = "Digital Devices"
    }

    // Society/Politics/Economy
    data object Society {
        const val POLITICS = "Politics/Administration"
        const val ECONOMICS = "Economics/Finance"
        const val LAW = "Law/Consulting"
        const val LABOR = "Labor/Workplace"
        const val SOCIAL = "Social Issues"
    }

    // Life/Health
    data object Life {
        const val HEALTH = "Health/Medicine"
        const val FOOD = "Food/Cooking"
        const val FASHION = "Fashion/Beauty"
        const val HOUSING = "Housing/Interior"
        const val PET = "Pets"
    }

    // Culture/Arts
    data object Culture {
        const val MUSIC = "Music"
        const val MOVIE = "Movies"
        const val ARTS = "Art/Design"
        const val PERFORMANCE = "Performance/Exhibition"
        const val ENTERTAINMENT = "Entertainment/Broadcasting"
    }

    // Sports/Leisure
    data object Sports {
        const val EXERCISE = "Exercise/Sports"
        const val LEISURE = "Leisure/Recreation"
        const val TRAVEL = "Travel"
        const val EXTREME = "Extreme Sports"
        const val ESPORTS = "eSports"
    }

    // Hobbies/Lifestyle
    data object Hobby {
        const val GAME = "Gaming"
        const val CRAFTS = "Crafts/Hobbies"
        const val COLLECTION = "Collection/Records"
        const val PHOTO = "Photography/Video"
        const val BOOKS = "Books"
    }

    // Science/Technology
    data object Science {
        const val PHYSICS = "Physics/Chemistry"
        const val BIO = "Biology/Life Sciences"
        const val EARTH = "Earth/Environment"
        const val ENGINEERING = "Engineering"
        const val MATH = "Mathematics"
    }

    fun getAllFields(): List<String> {
        return listOf(
            Education.ELEMENTARY, Education.SECONDARY, Education.COLLEGE,
            Education.LANGUAGE, Education.STUDY_ABROAD,

            IT.PROGRAMMING, IT.HARDWARE, IT.MOBILE,
            IT.SECURITY, IT.DIGITAL,

            Society.POLITICS, Society.ECONOMICS, Society.LAW,
            Society.LABOR, Society.SOCIAL,

            Life.HEALTH, Life.FOOD, Life.FASHION,
            Life.HOUSING, Life.PET,

            Culture.MUSIC, Culture.MOVIE, Culture.ARTS,
            Culture.PERFORMANCE, Culture.ENTERTAINMENT,

            Sports.EXERCISE, Sports.LEISURE, Sports.TRAVEL,
            Sports.EXTREME, Sports.ESPORTS,

            Hobby.GAME, Hobby.CRAFTS, Hobby.COLLECTION,
            Hobby.PHOTO, Hobby.BOOKS,

            Science.PHYSICS, Science.BIO, Science.EARTH,
            Science.ENGINEERING, Science.MATH
        )
    }

    fun getFieldsByCategory(): Map<String, List<String>> {
        return mapOf(
            "Education/Academics" to listOf(
                Education.ELEMENTARY, Education.SECONDARY, Education.COLLEGE,
                Education.LANGUAGE, Education.STUDY_ABROAD
            ),
            "Computer/Internet" to listOf(
                IT.PROGRAMMING, IT.HARDWARE, IT.MOBILE,
                IT.SECURITY, IT.DIGITAL
            ),
            "Society/Politics/Economy" to listOf(
                Society.POLITICS, Society.ECONOMICS, Society.LAW,
                Society.LABOR, Society.SOCIAL
            ),
            "Life/Health" to listOf(
                Life.HEALTH, Life.FOOD, Life.FASHION,
                Life.HOUSING, Life.PET
            ),
            "Culture/Arts" to listOf(
                Culture.MUSIC, Culture.MOVIE, Culture.ARTS,
                Culture.PERFORMANCE, Culture.ENTERTAINMENT
            ),
            "Sports/Leisure" to listOf(
                Sports.EXERCISE, Sports.LEISURE, Sports.TRAVEL,
                Sports.EXTREME, Sports.ESPORTS
            ),
            "Hobbies/Lifestyle" to listOf(
                Hobby.GAME, Hobby.CRAFTS, Hobby.COLLECTION,
                Hobby.PHOTO, Hobby.BOOKS
            ),
            "Science/Technology" to listOf(
                Science.PHYSICS, Science.BIO, Science.EARTH,
                Science.ENGINEERING, Science.MATH
            )
        )
    }
}