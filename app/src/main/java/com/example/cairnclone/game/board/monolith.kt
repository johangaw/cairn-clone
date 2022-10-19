package com.example.cairnclone.game

import com.example.cairnclone.game.board.Pos

data class Monolith(val pos: Pos, val type: MonolithType)

enum class MonolithLevel {
    Start,
    Beginner,
    Advanced
}

sealed class MonolithType(val name: String, val description: String, val level: MonolithLevel) {
    object ChaosOfTheGiants : MonolithType(
        "Chaos of the Giants",
        "Banish an enemy shaman that is in a space in your first row.",
        MonolithLevel.Start
    )

    object CairnOfDawn : MonolithType(
        "Cairn of Dawn",
        "Add a shaman from your village to a space in your first row.",
        MonolithLevel.Start
    )

    object CromlechOfTheStars : MonolithType(
        "Cromlech of the Stars",
        "Move the shaman from this megalith to another megalith.",
        MonolithLevel.Beginner
    )

    object PillarsOfSpring : MonolithType(
        "Pillars of Spring",
        "After this turn, it is your turn.",
        MonolithLevel.Beginner
    )

    object AlleyOfDusk : MonolithType(
        "Alley of Dusk",
        "Banish an enemy shaman adjacent to this megalith.",
        MonolithLevel.Beginner,
    )

    object DeerRock : MonolithType(
        "Deer Rock",
        "Move a shaman adjacent to this megalith one space.",
        MonolithLevel.Beginner
    )

    object MenhirOfTheDancers : MonolithType(
        "Menhir of the Dancers",
        "Move the shaman from this megalith one space.",
        MonolithLevel.Beginner
    )
//
//    object SanctuaryOfTheAges : MonolithType(
//        "Sanctuary of the Ages",
//        "Move a megalith one space.",
//        MonolithLevel.Beginner
//    )
//
//    object TumulusOfShadows : MonolithType(
//        "Tumulus of Shadows",
//        "Banish the shaman from this megalith.",
//        MonolithLevel.Advanced
//    )
//
//    object Stormwell : MonolithType(
//        "Stormwell",
//        "Swap the locations of two megaliths in the field.",
//        MonolithLevel.Advanced
//    )
//
//    object FairiesCircle : MonolithType(
//        "Fairies Circle",
//        "Move an enemy shaman one space.",
//        MonolithLevel.Advanced
//    )
//
//    object HavenOfPurity : MonolithType(
//        "Haven of Purity",
//        "Move another friendly shaman one space.",
//        MonolithLevel.Advanced
//    )
//
//    object MemorialMound : MonolithType(
//        "Memorial Mound",
//        "Flip an Action tile.",
//        MonolithLevel.Advanced
//    )
//
//    object SourceOfSilver : MonolithType(
//        "Source of Silver",
//        "Replace a megalith in the field with one of the two upcoming megaliths. Tuck the replaced megalith under the deck and draw a new upcoming megalith.",
//        MonolithLevel.Advanced
//    )

    companion object {
        fun getAll(): List<MonolithType> = listOf(
            ChaosOfTheGiants,
            CairnOfDawn,
            CromlechOfTheStars,
            PillarsOfSpring,
            AlleyOfDusk,
            DeerRock,
            MenhirOfTheDancers,
//            SanctuaryOfTheAges,
//            TumulusOfShadows,
//            Stormwell,
//            FairiesCircle,
//            HavenOfPurity,
//            MemorialMound,
//            SourceOfSilver,
        )
    }
}