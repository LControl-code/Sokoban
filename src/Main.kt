import kotlin.system.exitProcess

val maps =
        listOf(
                """
    ▒▒▒▒▒▒▒▒▒▒
    ▒   ▒▒▒▒▒▒
    ▒   ▒ ▲ ▒▒
    ▒ ◽◎ ◎▒  ▒
    ▒  ▒     ▒
    ▒ ▒◽◉▒◎  ▒
    ▒ ▒  ▒  ▒▒
    ▒   ▒▒◉ ▒▒
    ▒ ▒ ◎   ▒▒
    ▒       ▒▒
    ▒▒▒▒▒▒▒▒▒▒
""".trimIndent(),
                """
    ▒▒▒▒
    ▒ ◽▒
    ▒  ▒▒▒
    ▒◉ ▲ ▒
    ▒  ◎ ▒
    ▒  ▒▒▒
    ▒▒▒▒
    """.trimIndent(),
                """
    ▒▒▒▒▒▒
    ▒    ▒
    ▒ ▒▲ ▒
    ▒ ◎◉ ▒
    ▒ ◽◉ ▒
    ▒    ▒
    ▒▒▒▒▒▒
    """.trimIndent(),
                """
      ▒▒▒▒
    ▒▒▒  ▒▒▒▒
    ▒     ◎ ▒
    ▒ ▒  ▒◎ ▒
    ▒ ◽ ◽▒▲ ▒
    ▒▒▒▒▒▒▒▒▒
    """.trimIndent()
        )

fun readChar(): Char {
    val input = readlnOrNull() ?: ""
    return if (input.isEmpty()) '\u0000' else input[0]
}

data class Player(var row: Int, var col: Int)

class SokobanGame(private val mapIndex: Int) {
    private val map: String = maps[mapIndex]
    private var grid: Array<CharArray> = map.lines().map { it.toCharArray() }.toTypedArray()
    private var player = Player(0, 0)
    private val targets = mutableListOf<Pair<Int, Int>>()
    private var stepsCounter = 0

    init {
        initializeGrid()
    }

    private fun initializeGrid() {
        grid = maps[mapIndex].lines().map { it.toCharArray() }.toTypedArray()
        player.row = grid.indices.first { '▲' in grid[it] }
        player.col = grid[player.row].indices.first { grid[player.row][it] == '▲' }
        targets.clear()
        stepsCounter = 0

        for (i in grid.indices) {
            for (j in grid[i].indices) {
                when (grid[i][j]) {
                    '◽', '◉' -> {
                        targets.add(Pair(i, j))
                    }
                }
            }
        }
    }

    private fun checkWinCondition(): Boolean {
        return targets.all { (row, col) -> grid[row][col] == '◉' }
    }
    private fun printGrid() {
        grid.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                when (cell) {
                    '▲' -> print("\u001B[34m$cell \u001B[0m") // Blue
                    '◎' -> print("\u001B[31m$cell \u001B[0m") // Red
                    else -> {
                        if (targets.contains(Pair(i, j))) {
                            if (cell == '◉') {
                                print("\u001B[35m◉ \u001B[0m") // Purple
                            } else {
                                print("\u001B[35m$cell \u001B[0m") // Purple
                            }
                        } else {
                            print("\u001B[33m$cell \u001B[0m") // Yellow
                        }
                    }
                }
            }
            println()
        }
    }

    private fun movePlayerTo(newRow: Int, newCol: Int) {
        if (targets.contains(Pair(player.row, player.col))) {
            grid[player.row][player.col] = '◽'
        } else {
            grid[player.row][player.col] = ' '
        }
        grid[newRow][newCol] = '▲'
        player.row = newRow
        player.col = newCol
    }

    private fun movePlayer(deltaRow: Int, deltaCol: Int) {
        // Move the player if the new position is within bounds and unoccupied
        val newRow = player.row + deltaRow
        val newCol = player.col + deltaCol

        if (isValidMove(newRow, newCol)) {
            if (grid[newRow][newCol] == '◎' || grid[newRow][newCol] == '◉') {
                val nextRow = newRow + deltaRow
                val nextCol = newCol + deltaCol

                if (isValidMove(nextRow, nextCol) &&
                                grid[nextRow][nextCol] != '◎' &&
                                grid[nextRow][nextCol] != '◉'
                ) {
                    // Increment the counter each time a valid move is made
                    stepsCounter++

                    grid[nextRow][nextCol] =
                            if (targets.contains(Pair(nextRow, nextCol))) '◉' else '◎'
                    if (targets.contains(Pair(player.row, player.col))) {
                        grid[player.row][player.col] = '◽'
                    }
                    movePlayerTo(newRow, newCol)
                }
            } else {
                if (targets.contains(Pair(player.row, player.col))) {
                    grid[player.row][player.col] = '◽'
                }
                stepsCounter++

                movePlayerTo(newRow, newCol)
            }

            if (checkWinCondition()) {
                printGrid()
                println("You've won the game in $stepsCounter steps!")
                exitProcess(0)
            }
        }
    }

    private fun isValidMove(row: Int, col: Int): Boolean {
        // Check if the new position is within bounds and unoccupied
        return row in grid.indices && col in grid[row].indices && grid[row][col] != '▒'
    }

    fun play() {
        // val scanner = Scanner(System.`in`)

        while (true) {
            printGrid()
            // Get user input for movement
            println("Enter movement (W/A/S/D): ")
            val input: Char = readChar().uppercaseChar()

            // Process user input
            when (input) {
                'W' -> movePlayer(-1, 0)
                'A' -> movePlayer(0, -1)
                'S' -> movePlayer(1, 0)
                'D' -> movePlayer(0, 1)
                'R' -> initializeGrid()
                'Q' -> break
                else -> println("Invalid input. Use W/A/S/D to move.")
            }
        }
    }
}

fun main() {
    val sokobanGame = SokobanGame(2)
    sokobanGame.play()
}
