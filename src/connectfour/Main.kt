package connectfour

const val verticalBar = "║"
const val space = ' '
const val bottomLeftCorner = "╚"
const val bottomRightCorner = "╝"
const val spacer = "═"
const val columnsSeparator = "╩"
const val defaultRows = 6
const val defaultCols = 7
val validSizeRange = 5..9
const val FIRST_PLAYER_DISC = 'o'
const val SECOND_PLAYER_DISC = '*'
const val END_GAME = "end"
var numberOfDiscs = 0;

fun main() {
    println("Connect Four")
    val (firstPlayerName, secondPlayerName) = readPlayerNames()
    val (rowsNumber, columnsNumber) = readBoardSize()
    val numberOfGames = readNumberOfGames()
    printGameMessage(
        firstPlayerName,
        secondPlayerName,
        rowsNumber,
        columnsNumber,
        numberOfGames
    )
    play(firstPlayerName, secondPlayerName, numberOfGames, rowsNumber, columnsNumber)
}

fun play(
    firstPlayerName: String,
    secondPlayerName: String,
    numberOfGames: Int,
    rowsNumber: Int,
    columnsNumber: Int
) {
    var gameNumber = 1
    var choice = ""
    val scores = mutableMapOf<String, Int>()
    while (choice != END_GAME && gameNumber <= numberOfGames) {
        val board = Array(rowsNumber) { CharArray(columnsNumber) { space } }
        numberOfDiscs = 0
        if (numberOfGames > 1) {
            println("Game #$gameNumber")
        }
        printBoard(board)
        var currentPlayer =
            if (gameNumber % 2 != 0) firstPlayerName else secondPlayerName
        choice = readValidPlayerChoice(currentPlayer, board)
        while (choice != END_GAME) {
            val disc =
                if (currentPlayer == firstPlayerName) FIRST_PLAYER_DISC else SECOND_PLAYER_DISC
            val won = addDiscToBoard(choice, disc, board)
            printBoard(board)
            if (won) {
                println("Player $currentPlayer won")
                scores.merge(currentPlayer, 2, Int::plus)
                println(
                    "Score\n$firstPlayerName: ${
                        scores.getOrDefault(
                            firstPlayerName,
                            0
                        )
                    } $secondPlayerName: ${scores.getOrDefault(secondPlayerName, 0)}"
                )
                break
            }
            if (numberOfDiscs == board.size * board[0].size) {
                println("It is a draw")
                scores.merge(firstPlayerName, 1, Int::plus)
                scores.merge(secondPlayerName, 1, Int::plus)
                println(
                    "Score\n$firstPlayerName: ${
                        scores.getOrDefault(
                            firstPlayerName,
                            0
                        )
                    } $secondPlayerName: ${scores.getOrDefault(secondPlayerName, 0)}"
                )
                break
            }
            currentPlayer =
                if (currentPlayer == firstPlayerName) secondPlayerName else firstPlayerName
            choice = readValidPlayerChoice(currentPlayer, board)
        }
        gameNumber++
    }
    println("Game over!")
}

fun addDiscToBoard(columnChoice: String, disc: Char, board: Array<CharArray>): Boolean {
    val realColumnNumber = columnChoice.toInt() - 1
    nextAvailablePosition(realColumnNumber, board).let {
        board[it!!.first][it.second] = disc
        numberOfDiscs++
        return checkForWin(it, board)
    }
}

fun readNumberOfGames(): Int {
    println(
        """
        Do you want to play single or multiple games?
        For a single game, input 1 or press Enter
        Input a number of games:
    """.trimIndent()
    )
    var option = readLine()!!
    if (option.isEmpty()) {
        return 1
    }
    val digits = Regex("\\d+")
    while (!digits.matches(option) || option.toInt() <= 0) {
        println("Invalid input")
        println(
            """
        Do you want to play single or multiple games?
        For a single game, input 1 or press Enter
        Input a number of games:
    """.trimIndent()
        )
        option = readLine() ?: return 1
    }
    return option.toInt()
}

fun checkForWin(coord: Pair<Int, Int>, board: Array<CharArray>): Boolean {
    val line = coord.first
    val col = coord.second
    var count = 1
    // check the line for 4 contiguous discs of the same type
    for (j in 0 until board[0].lastIndex) {
        val cell = board[line][j]
        val nextCell = board[line][j + 1]
        if (cell != space && nextCell != space && cell == nextCell) {
            count++
            if (count == 4) {
                return true
            }
        } else {
            count = 1
        }
    }
    count = 1
    // check the column for 4 contiguous discs of the same type
    for (i in 0 until board.lastIndex) {
        val cell = board[i][col]
        val nextCell = board[i + 1][col]
        if (cell != space && nextCell != space && cell == nextCell) {
            count++
            if (count == 4) {
                return true
            }
        } else {
            count = 1
        }
    }

    val diagonals = findDiagonals(coord, board)
    // check the diagonals for 4 contiguous discs of the same type
    for (diagonal in diagonals) {
        val dir = diagonal.key
        var lineIdx = diagonal.value.first
        var colIdx = diagonal.value.second
        when (dir) {
            "ne" -> {
                count = 1
                while (lineIdx < board.lastIndex && colIdx > 0) {
                    val cell = board[lineIdx][colIdx]
                    val nextCell = board[lineIdx + 1][colIdx - 1]
                    if (cell != space && nextCell != space && cell == nextCell) {
                        count++
                        if (count == 4) {
                            return true
                        }
                    } else {
                        count = 1
                    }
                    lineIdx++
                    colIdx--
                }
            }
            "nw" -> {
                count = 1
                while (lineIdx < board.lastIndex && colIdx < board[0].lastIndex) {
                    val cell = board[lineIdx][colIdx]
                    val nextCell = board[lineIdx + 1][colIdx + 1]
                    if (cell != space && nextCell != space && cell == nextCell) {
                        count++
                        if (count == 4) {
                            return true
                        }
                    } else {
                        count = 1
                    }
                    lineIdx++
                    colIdx++
                }
            }
        }
    }

    return false
}

private fun findDiagonals(
    coord: Pair<Int, Int>,
    board: Array<CharArray>
): MutableMap<String, Pair<Int, Int>> {
    var pos = coord.copy()
    val lastColIdx = board[0].lastIndex
    val result = mutableMapOf<String, Pair<Int, Int>>()

    // walk NE
    while (pos.first != 0 && pos.second != lastColIdx) {
        pos = pos.copy(pos.first - 1, pos.second + 1)
    }
    result["ne"] = pos

    pos = coord.copy()
    // walk NW
    while (pos.first != 0 && pos.second != 0) {
        pos = pos.copy(pos.first - 1, pos.second - 1)
    }
    result["nw"] = pos

    return result
}

private fun readValidPlayerChoice(
    playerName: String,
    board: Array<CharArray>
): String {
    println("$playerName's turn:")
    var columnChoice = readLine()!!
    while (columnChoice != END_GAME && !isValidChoice(columnChoice, board)) {
        println("$playerName's turn:")
        columnChoice = readLine()!!
    }
    return columnChoice
}

fun isValidChoice(choice: String, board: Array<CharArray>): Boolean {
    val onlyDigit = "\\d+".toRegex()
    if (!onlyDigit.matches(choice)) {
        println("Incorrect column number")
        return false
    }
    val cols = board[0].size
    if (choice.toInt() !in 1..cols) {
        println("The column number is out of range (1 - $cols)")
        return false
    }
    val realColNumber = choice.toInt() - 1
    if (nextAvailablePosition(realColNumber, board) == null) {
        println("Column $choice is full")
        return false
    }
    return true
}

fun nextAvailablePosition(
    realColNumber: Int,
    board: Array<CharArray>
): Pair<Int, Int>? {
    var rIdx = 0
    while (rIdx <= board.lastIndex && board[rIdx][realColNumber] == space) {
        rIdx++
    }
    return if (rIdx == 0) null else Pair(--rIdx, realColNumber)
}

private fun readPlayerNames(): Pair<String, String> {
    println("First player's name:")
    val firstPlayerName = readLine()!!
    println("Second player's name:")
    val secondPlayerName = readLine()!!
    return Pair(firstPlayerName, secondPlayerName)
}

private fun readBoardSize(): Pair<Int, Int> {
    var rows = 0
    var cols = 0
    var validDimensions = false
    while (!validDimensions) {
        println(
            """
        Set the board dimensions (Rows x Columns)
        Press Enter for default (6 x 7)
    """.trimIndent()
        )
        val dimensions = readLine()!!
        if (dimensions.isEmpty()) {
            rows = defaultRows
            cols = defaultCols
            validDimensions = true
        } else {
            val regex = Regex("^(\\d+)\\s*[xX]\\s*(\\d+)$")
            val groupValues = regex.find(dimensions.trim())?.groupValues
            rows = groupValues?.get(1)?.toInt() ?: -1
            cols = groupValues?.get(2)?.toInt() ?: -1
            if (rows < 0 || cols < 0) {
                println("Invalid input")
            } else {
                if (rows !in validSizeRange) {
                    println("Board rows should be from 5 to 9")
                } else if (cols !in validSizeRange) {
                    println("Board columns should be from 5 to 9")
                } else {
                    validDimensions = true
                }
            }
        }
    }
    return Pair(rows, cols)
}

private fun printGameMessage(
    firstPlayerName: String,
    secondPlayerName: String,
    rows: Int,
    cols: Int,
    numberOfGames: Int
) {
    println(
        """
        $firstPlayerName VS $secondPlayerName
        $rows X $cols board
        ${if (numberOfGames == 1) "Single game" else "Total $numberOfGames games"}
    """.trimIndent()
    )
}

private fun printBoard(board: Array<CharArray>) {
    val rows = board.size
    val cols = board[0].size
    println("$space${(1..cols).joinToString(space.toString())}")
    for (i in 1..rows) {
        for (j in 1..cols + 1) {
            if (j < cols + 1) print("$verticalBar${board[i - 1][j - 1]}") else print(
                verticalBar
            )
        }
        println()
    }
    val bottom =
        (1..cols + 1).joinToString("") { if (it == 1) "$bottomLeftCorner$spacer" else if (it == cols + 1) bottomRightCorner else "$columnsSeparator$spacer" }
    println(bottom)
}
