package com.example.cairnclone.game.board

import com.example.cairnclone.game.Monolith
import com.example.cairnclone.game.MonolithType
import kotlin.random.Random

data class BoardState(
    val activeTeam: Team,
    val board: Board,
    val spawnActionTile: SpawnActionTile,
    val moveActionTile: MoveActionTile,
    val transformationTile: TransformationTile,
    val inactiveShamans: List<Shaman>,
    val activeShamans: List<Shaman>,
    val activeMonoliths: List<Monolith>,
    val monolithsStack: List<MonolithType>,
    val scores: Scores,
    val movedShamanIds: List<ShamanId>,
) {
    fun shamanAt(pos: Pos): Shaman? = activeShamans.find { it.pos == pos }
    fun isInVillage(pos: Pos, team: Team) = board.villageIndex[team] == pos.y
    fun monolithAt(pos: Pos) = activeMonoliths.find { it.pos == pos }
    val upcomingMonoliths get() = monolithsStack.take(2)
}

class BoardStateBuilder {
    private var boardState: BoardState
    private val MAX_SHAMANS = 5

    init {
        boardState = BoardState(
            activeTeam = Team.Forest,
            board = Board(),
            spawnActionTile = SpawnActionTile.SpawnBlack,
            moveActionTile = MoveActionTile.Orthogonally,
            transformationTile = TransformationTile.Surrounded,
            inactiveShamans = emptyList(),
            activeShamans = emptyList(),
            activeMonoliths = emptyList(),
            monolithsStack = emptyList(),
            scores = Scores(Score(0), Score(0)),
            movedShamanIds = emptyList()
        )
    }

    fun build(): BoardState = boardState

    fun addMonolithStack(randomize: Boolean = false) {
        val activeMonolithTypes = boardState.activeMonoliths.map { it.type }
        boardState = boardState.copy(
            monolithsStack = MonolithType.getAll().let { if (randomize) it.shuffled() else it }
                .filter { !activeMonolithTypes.contains(it) }
        )
    }

    fun positionForestShaman(pos: Pos) = positionShaman(Team.Forest, pos)
    fun positionSeaShaman(pos: Pos) = positionShaman(Team.Sea, pos)
    fun positionShaman(team: Team, pos: Pos) {
        val (active, activeOthers) = boardState.activeShamans.partition { it.team == team }
        val (inactive, inactiveOther) = boardState.inactiveShamans.partition { it.team == team }
        boardState = boardState.copy(
            activeShamans = activeOthers + (active + Shaman(team = team, pos = pos))
                .drop((active.size + 1 - MAX_SHAMANS).coerceAtLeast(0)),
            inactiveShamans = inactiveOther + inactive
                .drop((inactive.size + active.size + 1 - MAX_SHAMANS).coerceAtLeast(0))
        )
    }

    fun addInactiveShamans() {
        fun createInactiveShamans(team: Team) =
            (boardState.activeShamans.count { it.team == Team.Sea } until MAX_SHAMANS).map {
                Shaman(
                    team = Team.Sea,
                    pos = Pos(0, 0)
                )
            }
        boardState = boardState.copy(
            inactiveShamans = createInactiveShamans(Team.Forest) + createInactiveShamans(Team.Sea)
        )
    }

    fun positionMonolith(type: MonolithType, pos: Pos) {
        boardState = boardState.copy(
            activeMonoliths = boardState.activeMonoliths.filter { it.type != type } + Monolith(
                pos,
                type
            ),
            monolithsStack = boardState.monolithsStack.filter { it != type }
        )
    }

    fun positionStartShamans() {
        positionForestShaman(Pos(0, 0))
        positionForestShaman(Pos(2, 0))
        positionForestShaman(Pos(4, 0))

        positionSeaShaman(Pos(0, 4))
        positionSeaShaman(Pos(2, 4))
        positionSeaShaman(Pos(4, 4))

        addInactiveShamans()
    }

    fun positionStartMonoliths() {
        val (m1, m2) = boardState.monolithsStack.take(2)
        positionMonolith(m1, Pos(1, 2))
        positionMonolith(m2, Pos(3, 2))
    }

    fun transformation(tile: TransformationTile) {
        boardState = boardState.copy(
            transformationTile = tile
        )
    }

    fun moveAction(tile: MoveActionTile) {
        boardState = boardState.copy(
            moveActionTile = tile
        )
    }

    fun spawnAction(tile: SpawnActionTile) {
        boardState = boardState.copy(
            spawnActionTile = tile
        )
    }

    fun emptyBoard(randomize: Boolean = false) {
        addMonolithStack(randomize)
        transformation(if (randomize && Random.nextBoolean()) boardState.transformationTile.flip() else boardState.transformationTile)
        moveAction(if (randomize && Random.nextBoolean()) boardState.moveActionTile.flip() else boardState.moveActionTile)
        spawnAction(if (randomize && Random.nextBoolean()) boardState.spawnActionTile.flip() else boardState.spawnActionTile)
    }
}

fun buildBoard(init: BoardStateBuilder.() -> Unit): BoardState {
    val builder = BoardStateBuilder()
    builder.init()
    return builder.build()
}
