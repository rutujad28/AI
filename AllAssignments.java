//TicTacToeAI
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class TicTacToeAI {
    private static final int HUMAN = -1;
    private static final int COMP = +1;
    private static final int[][] board = new int[3][3];

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        char h_choice = ' '; // human
        char c_choice = ' '; // computer
        char first = ' '; // who will play first

        // Human chooses X or O to play
        while (h_choice != 'O' && h_choice != 'X') {
            System.out.print("Choose X or O\nChosen: ");
            h_choice = scanner.next().toUpperCase().charAt(0);
        }

        // Setting computer's choice
        if (h_choice == 'X') {
            c_choice = 'O';
        } else {
            c_choice = 'X';
        }

        // Human may start first
        System.out.println("First to start?[y/n]: ");
        while (first != 'Y' && first != 'N') {
            try {
                String input = scanner.nextLine().toUpperCase();
                if (input.length() > 0) {
                    first = input.charAt(0);
                }
            } catch (Exception e) {
                System.out.println("Bad choice");
            }
        }

        // Main loop of this game
        while (emptyCells(board).size() > 0 && !gameOver(board)) {
            if (first == 'N') {
                aiTurn(c_choice, h_choice);
                first = ' ';
            }

            humanTurn(c_choice, h_choice, scanner);
            aiTurn(c_choice, h_choice);
        }

        // Game over message
        if (wins(board, HUMAN)) {
            System.out.println("Human Turn" + h_choice);
            render(board, c_choice, h_choice);
            System.out.println("YOU WIN! How is this Possible?");
        } else if (wins(board, COMP)) {
            render(board, c_choice, h_choice);
            System.out.println("YOU LOSE!");
        } else {
            render(board, c_choice, h_choice);
            System.out.println("DRAW!");
        }
    }

    private static int evaluate(int[][] state) {
        if (wins(state, COMP)) {
            return +1;
        } else if (wins(state, HUMAN)) {
            return -1;
        } else {
            return 0;
        }
    }

    private static boolean wins(int[][] state, int player) {
        int[][] winState = {
                { state[0][0], state[0][1], state[0][2] },
                { state[1][0], state[1][1], state[1][2] },
                { state[2][0], state[2][1], state[2][2] },
                { state[0][0], state[1][0], state[2][0] },
                { state[0][1], state[1][1], state[2][1] },
                { state[0][2], state[1][2], state[2][2] },
                { state[0][0], state[1][1], state[2][2] },
                { state[2][0], state[1][1], state[0][2] }
        };
        for (int[] row : winState) {
            if (row[0] == player && row[1] == player && row[2] == player) {
                return true;
            }
        }
        return false;
    }

    private static boolean gameOver(int[][] state) {
        return wins(state, HUMAN) || wins(state, COMP);
    }

    private static List<int[]> emptyCells(int[][] state) {
        List<int[]> cells = new ArrayList<>();
        for (int x = 0; x < state.length; x++) {
            for (int y = 0; y < state[0].length; y++) {
                if (state[x][y] == 0) {
                    cells.add(new int[] { x, y });
                }
            }
        }
        return cells;
    }

    private static boolean validMove(int x, int y) {
        List<int[]> cells = emptyCells(board);
        for (int[] cell : cells) {
            if (cell[0] == x && cell[1] == y) {
                return true;
            }
        }
        return false;
    }

    private static boolean setMove(int x, int y, int player) {
        if (validMove(x, y)) {
            board[x][y] = player;
            return true;
        } else {
            return false;
        }

    }

    private static int[] minimax(int[][] state, int depth, int player) {
        int[] best = { -1, -1, (player == COMP) ? Integer.MIN_VALUE : Integer.MAX_VALUE };

        if (depth == 0 || gameOver(state)) {
            int score = evaluate(state);
            return new int[] { -1, -1, score };
        }

        for (int[] cell : emptyCells(state)) {
            int x = cell[0];
            int y = cell[1];
            state[x][y] = player;
            int[] score = minimax(state, depth - 1, -player);
            state[x][y] = 0;
            score[0] = x;
            score[1] = y;

            if (player == COMP) {
                if (score[2] > best[2]) {
                    best = score; // max value
                }
            } else {
                if (score[2] < best[2]) {
                    best = score; // min value
                }
            }
        }
        return best;
    }

    public static void render(int[][] state, char cChoice, char hChoice) {
        Map<Integer, Character> chars = new HashMap<>();
        chars.put(-1, hChoice);
        chars.put(1, cChoice);
        chars.put(0, ' ');

        String strLine = "---------------";

        System.out.println("\n" + strLine);
        for (int[] row : state) {
            for (int cell : row) {
                char symbol = chars.get(cell);
                System.out.print("| " + symbol + " |");
            }
            System.out.println("\n" + strLine);
        }
    }

    private static void aiTurn(char cChoice, char hChoice) {
        int depth = emptyCells(board).size();
        if (depth == 0 || gameOver(board)) {
            return;
        }

        System.out.println("Computer turn [" + cChoice + "]");
        render(board, cChoice, hChoice);

        int[] bestMove;
        if (depth == 9) {
            Random rand = new Random();
            int x = rand.nextInt(3);
            int y = rand.nextInt(3);
            bestMove = new int[] { x, y };
        } else {
            bestMove = findBestMove(board, HUMAN);
        }

        setMove(bestMove[0], bestMove[1], COMP);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void humanTurn(char cChoice, char hChoice, Scanner scanner) {
        int depth = emptyCells(board).size();
        if (depth == 0 || gameOver(board)) {
            return;
        }

        int move = -1;
        int[][] moves = {
                { 0, 0 }, { 0, 1 }, { 0, 2 },
                { 1, 0 }, { 1, 1 }, { 1, 2 },
                { 2, 0 }, { 2, 1 }, { 2, 2 }
        };

        System.out.println("Human turn [" + hChoice + "]");
        render(board, cChoice, hChoice);

        while (move < 1 || move > 9) {
            try {
                System.out.print("Use numpad (1..9): ");
                move = scanner.nextInt();
                int[] coord = moves[move - 1];
                boolean canMove = setMove(coord[0], coord[1], HUMAN);

                if (!canMove) {
                    System.out.println("Bad move");
                    move = -1;
                }
            } catch (Exception e) {
                System.out.println("Bad choice");
                scanner.next();
            }
        }
    }

    private static int[] findBestMove(int[][] board, int player) {
        int[] bestChild = null;
        int previous = Integer.MIN_VALUE;
        List<int[]> positions = emptyCells(board);
        for (int[] position : positions) {
            int x = position[0];
            int y = position[1];
            board[x][y] = COMP;
            int[] current = minimax(board, emptyCells(board).size(), HUMAN);
            board[x][y] = 0;
            System.out.println("Heuristic Value: " + current[2]);
            if (current[2] > previous) {
                bestChild = position;
                previous = current[2];
            }
        }
        return bestChild;
    }
}







//TicTacToeNonAI
#include <stdio.h>
#include <conio.h>
#include <stdlib.h>
#include <windows.h>

int board[10] = {2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
int turn = 1, flag = 0;
int player, comp;

void menu();
void go(int n);
void start_game();
void check_draw();
void draw_board();
void player_first();
void put_X_O(char ch, int pos);
COORD coord = {0, 0};

void gotoxy(int x, int y)
{
    coord.X = x;
    coord.Y = y;
    SetConsoleCursorPosition(GetStdHandle(STD_OUTPUT_HANDLE), coord);
}

void main()
{
    system("cls");
    menu();
    getch();
}

void menu()
{
    int choice;
    system("cls");
    printf("\n--------MENU--------");
    printf("\n1 : Play with X");
    printf("\n2 : Play with O");
    printf("\n3 : Exit");
    printf("\nEnter your choice:>");
    scanf("%d", &choice);
    turn = 1;
    switch (choice)
    {
    case 1:
        player = 1;
        comp = 0;
        player_first();
        break;
    case 2:
        player = 0;
        comp = 1;
        start_game();
        break;
    case 3:
        exit(1);
    default:
        menu();
    }
}

int make2()
{
    if (board[5] == 2)
        return 5;
    if (board[1] == 2)
        return 1;
    if (board[3] == 2)
        return 3;
    if (board[7] == 2)
        return 7;
    if (board[9] == 2)
        return 9;
    return 0;
}

int make4()
{
    if (board[2] == 2)
        return 2;
    if (board[4] == 2)
        return 4;
    if (board[6] == 2)
        return 6;
    if (board[8] == 2)
        return 8;
    return 0;
}

int posswin(int p)
{
    // p==1 then X   p==0  then  O
    int i;
    int check_val, pos;

    if (p == 1)
        check_val = 18;
    else
        check_val = 50;

    i = 1;
    while (i <= 9)
    {
        if (board[i] * board[i + 1] * board[i + 2] == check_val)
        {
            if (board[i] == 2)
                return i;
            if (board[i + 1] == 2)
                return i + 1;
            if (board[i + 2] == 2)
                return i + 2;
        }
        i += 3;
    }

    i = 1;
    while (i <= 3)
    {
        if (board[i] * board[i + 3] * board[i + 6] == check_val)
        {
            if (board[i] == 2)
                return i;
            if (board[i + 3] == 2)
                return i + 3;
            if (board[i + 6] == 2)
                return i + 6;
        }
        i++;
    }

    if (board[1] * board[5] * board[9] == check_val)
    {
        if (board[1] == 2)
            return 1;
        if (board[5] == 2)
            return 5;
        if (board[9] == 2)
            return 9;
    }

    if (board[3] * board[5] * board[7] == check_val)
    {
        if (board[3] == 2)
            return 3;
        if (board[5] == 2)
            return 5;
        if (board[7] == 2)
            return 7;
    }
    return 0;
}

void go(int n)
{
    if (turn % 2)
        board[n] = 3;
    else
        board[n] = 5;
    turn++;
}

void player_first()
{
    int pos;

    check_draw();
    draw_board();
    gotoxy(30, 18);
    printf("Your Turn :> ");
    scanf("%d", &pos);

    if (board[pos] != 2)
        player_first();

    if (pos == posswin(player))
    {
        go(pos);
        draw_board();
        gotoxy(30, 20);
        printf("Player Wins");
        getch();
        exit(0);
    }

    go(pos);
    draw_board();
    start_game();
}

void start_game()
{
    // p==1 then X   p==0  then  O
    if (posswin(comp))
    {
        go(posswin(comp));
        flag = 1;
    }
    else if (posswin(player))
        go(posswin(player));
    else if (make2())
        go(make2());
    else
        go(make4());
    draw_board();

    if (flag)
    {
        gotoxy(30, 20);
        printf("Computer wins");
        getch();
    }
    else
        player_first();
}

void check_draw()
{
    if (turn > 9)
    {
        gotoxy(30, 20);
        printf("Game Draw");
        getch();
        exit(0);
    }
}

void draw_board()
{
    int j;

    for (j = 9; j < 17; j++)
    {
        gotoxy(35, j);
        printf("|       |");
    }
    gotoxy(28, 11);
    printf("-----------------------");
    gotoxy(28, 14);
    printf("-----------------------");

    for (j = 1; j < 10; j++)
    {
        if (board[j] == 3)
            put_X_O('X', j);
        else if (board[j] == 5)
            put_X_O('O', j);
    }
}

void put_X_O(char ch, int pos)
{
    int m;
    int x = 31, y = 10;

    m = pos;

    if (m > 3)
    {
        while (m > 3)
        {
            y += 3;
            m -= 3;
        }
    }
    if (pos % 3 == 0)
        x += 16;
    else
    {
        pos %= 3;
        pos--;
        while (pos)
        {
            x += 8;
            pos--;
        }
    }
    gotoxy(x, y);
    printf("%c", ch);
}





//Water Jar BFS
import java.util.*;

class Pair {
    int j1, j2;
    List<Pair> path;

    Pair(int j1, int j2) {
        this.j1 = j1;
        this.j2 = j2;
        path = new ArrayList<>();
    }

    Pair(int j1, int j2, List<Pair> _path) {
        this.j1 = j1;
        this.j2 = j2;

        path = new ArrayList<>();
        path.addAll(_path);
        path.add(new Pair(this.j1, this.j2));
    }
}

public class waterjarbfs {
    public static void main(String[] args)
            throws java.lang.Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter quantity of jug1, jug2 and target");

        int jug1 = sc.nextInt();

        int jug2 = sc.nextInt();

        int target = sc.nextInt();

        getPathIfPossible(jug1, jug2, target);
    }

    private static void getPathIfPossible(int jug1, int jug2, int target) {
        boolean[][] visited = new boolean[jug1 + 1][jug2 + 1];
        Queue<Pair> queue = new LinkedList<>();

        Pair initialState = new Pair(0, 0);
        initialState.path.add(new Pair(0, 0));
        queue.offer(initialState);

        while (!queue.isEmpty()) {
            Pair curr = queue.poll();

            if (curr.j1 > jug1 || curr.j2 > jug2 || visited[curr.j1][curr.j2])
                continue;
            visited[curr.j1][curr.j2] = true;

            if (curr.j1 == target || curr.j2 == target) {
                if (curr.j1 == target) {
                    curr.path.add(new Pair(curr.j1, 0));
                } else {
                    curr.path.add(new Pair(0, curr.j2));
                }
                int n = curr.path.size();
                System.out.println("Path of states of jugs followed is :");
                for (int i = 0; i < n; i++)
                    System.out.println(curr.path.get(i).j1 + " , " + curr.path.get(i).j2);
                return;
            }
            queue.offer(new Pair(jug1, 0, curr.path));
            queue.offer(new Pair(0, jug2, curr.path));

            queue.offer(new Pair(jug1, curr.j2, curr.path));
            queue.offer(new Pair(curr.j1, jug2, curr.path));

            queue.offer(new Pair(0, curr.j2, curr.path));
            queue.offer(new Pair(curr.j1, 0, curr.path));

            int emptyJug = jug2 - curr.j2;
            int amountTransferred = Math.min(curr.j1, emptyJug);
            int j2 = curr.j2 + amountTransferred;
            int j1 = curr.j1 - amountTransferred;
            queue.offer(new Pair(j1, j2, curr.path));

            emptyJug = jug1 - curr.j1;
            amountTransferred = Math.min(curr.j2, emptyJug);
            j2 = curr.j2 - amountTransferred;
            j1 = curr.j1 + amountTransferred;
            queue.offer(new Pair(j1, j2, curr.path));
        }

        System.out.println("Not Possible to obtain target");
    }
}





//Water Jar DFS
import java.util.*;

class Pair {
    int j1, j2;
    List<Pair> path;

    Pair(int j1, int j2) {
        this.j1 = j1;
        this.j2 = j2;
        path = new ArrayList<>();
    }

    Pair(int j1, int j2, List<Pair> _path) {
        this.j1 = j1;
        this.j2 = j2;

        path = new ArrayList<>();
        path.addAll(_path);
        path.add(new Pair(this.j1, this.j2));
    }
}

public class waterjardfs {
    public static void main(String[] args) throws java.lang.Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter quantity of jug1, jug2, and target");

        int jug1 = sc.nextInt();
        int jug2 = sc.nextInt();
        int target = sc.nextInt();

        getPathIfPossible(jug1, jug2, target);
    }

    private static void getPathIfPossible(int jug1, int jug2, int target) {
        boolean[][] visited = new boolean[jug1 + 1][jug2 + 1];
        Stack<Pair> stack = new Stack<>();

        Pair initialState = new Pair(0, 0);
        initialState.path.add(new Pair(0, 0));
        stack.push(initialState);

        while (!stack.isEmpty()) {
            Pair curr = stack.pop();

            if (curr.j1 > jug1 || curr.j2 > jug2 || visited[curr.j1][curr.j2])
                continue;
            visited[curr.j1][curr.j2] = true;

            if (curr.j1 == target || curr.j2 == target) {
                if (curr.j1 == target) {
                    curr.path.add(new Pair(curr.j1, 0));
                } else {
                    curr.path.add(new Pair(0, curr.j2));
                }
                int n = curr.path.size();
                System.out.println("Path of states of jugs followed is :");
                for (int i = 0; i < n; i++)
                    System.out.println(curr.path.get(i).j1 + " , " + curr.path.get(i).j2);
                return;
            }

            stack.push(new Pair(jug1, 0, curr.path));
            stack.push(new Pair(0, jug2, curr.path));
            stack.push(new Pair(jug1, curr.j2, curr.path));
            stack.push(new Pair(curr.j1, jug2, curr.path));
            stack.push(new Pair(0, curr.j2, curr.path));
            stack.push(new Pair(curr.j1, 0, curr.path));

            int emptyJug, amountTransferred, j1, j2;

            emptyJug = jug2 - curr.j2;
            amountTransferred = Math.min(curr.j1, emptyJug);
            j2 = curr.j2 + amountTransferred;
            j1 = curr.j1 - amountTransferred;
            stack.push(new Pair(j1, j2, curr.path));

            emptyJug = jug1 - curr.j1;
            amountTransferred = Math.min(curr.j2, emptyJug);
            j2 = curr.j2 - amountTransferred;
            j1 = curr.j1 + amountTransferred;
            stack.push(new Pair(j1, j2, curr.path));
        }

        System.out.println("Not Possible to obtain target");
    }
}




//Missionaries and Cannibles bfs
import java.util.*;

class Node {
    int missionaries;
    int cannibals;
    int boat; // 1 for the original side, 0 for the other side
    Node parent;

    public Node(int missionaries, int cannibals, int boat, Node parent) {
        this.missionaries = missionaries;
        this.cannibals = cannibals;
        this.boat = boat;
        this.parent = parent;
    }

    // Check if the current state is a valid state
    public boolean isValid() {
        if (missionaries < 0 || cannibals < 0 || missionaries > 3 || cannibals > 3) {
            return false;
        }

        // Check if missionaries are outnumbered by cannibals on either side
        if ((missionaries < cannibals && missionaries > 0) || (missionaries > cannibals && missionaries < 3)) {
            return false;
        }

        return true;
    }

    // Check if the current state is the goal state
    public boolean isGoal() {
        return missionaries == 0 && cannibals == 0 && boat == 0;
    }

    // Get possible next states from the current state
    public List<Node> getNextStates() {
        List<Node> nextStates = new ArrayList<>();

        int newBoat = 1 - boat; // Flip the boat side

        // Generate possible combinations of missionaries and cannibals to move
        for (int m = 0; m <= 2; m++) {
            for (int c = 0; c <= 2; c++) {
                if (m + c >= 1 && m + c <= 2) {
                    int newMissionaries = missionaries - m * boat + m * newBoat;
                    int newCannibals = cannibals - c * boat + c * newBoat;

                    Node nextState = new Node(newMissionaries, newCannibals, newBoat, this);

                    if (nextState.isValid()) {
                        nextStates.add(nextState);
                    }
                }
            }
        }

        return nextStates;
    }
}

public class mncbfs {
    public static void main(String[] args) {
        bfs();
    }

    public static void bfs() {
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        Node initialState = new Node(3, 3, 1, null);
        queue.add(initialState);
        visited.add(initialState);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (current.isGoal()) {
                printSolution(current);
                return;
            }

            List<Node> nextStates = current.getNextStates();

            for (Node nextState : nextStates) {
                if (!visited.contains(nextState)) {
                    queue.add(nextState);
                    visited.add(nextState);
                }
            }
        }

        System.out.println("No solution found.");
    }

    public static void printSolution(Node goalNode) {
        List<Node> path = new ArrayList<>();

        while (goalNode != null) {
            path.add(goalNode);
            goalNode = goalNode.parent;
        }

        Collections.reverse(path);

        for (Node node : path) {
            System.out.println("(" + node.missionaries + ", " + node.cannibals + ", " + node.boat + ")");
        }

        System.out.println("Goal State Reached!");
    }
}








//Missionaries and Cannibles dfs
import java.util.*;

class Node2 {
    int missionaries;
    int cannibals;
    int boat; // 1 for the original side, 0 for the other side
    Node2 parent;

    public Node2(int missionaries, int cannibals, int boat, Node2 parent) {
        this.missionaries = missionaries;
        this.cannibals = cannibals;
        this.boat = boat;
        this.parent = parent;
    }

    // Check if the current state is a valid state
    public boolean isValid() {
        if (missionaries < 0 || cannibals < 0 || missionaries > 3 || cannibals > 3) {
            return false;
        }

        // Check if missionaries are outnumbered by cannibals on either side
        return (missionaries >= cannibals || missionaries == 0) && (missionaries <= cannibals || missionaries == 3);
    }

    // Check if the current state is the goal state
    public boolean isGoal() {
        return missionaries == 0 && cannibals == 0 && boat == 0;
    }

    // Get possible next states from the current state
    public List<Node2> getNextStates() {
        List<Node2> nextStates = new ArrayList<>();

        int newBoat = 1 - boat; // Flip the boat side

        // Generate possible combinations of missionaries and cannibals to move
        for (int m = 0; m <= 2; m++) {
            for (int c = 0; c <= 2; c++) {
                if (m + c >= 1 && m + c <= 2) {
                    int newMissionaries = missionaries - m * boat + m * newBoat;
                    int newCannibals = cannibals - c * boat + c * newBoat;

                    Node2 nextState = new Node2(newMissionaries, newCannibals, newBoat, this);

                    if (nextState.isValid()) {
                        nextStates.add(nextState);
                    }
                }
            }
        }

        return nextStates;
    }

    // Override equals and hashCode methods to use in HashSet
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Node2 node = (Node2) obj;
        return missionaries == node.missionaries && cannibals == node.cannibals && boat == node.boat;
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionaries, cannibals, boat);
    }
}

public class mncdfs {
    public static void main(String[] args) {
        dfs();
    }

    public static void dfs() {
        Stack<Node2> stack = new Stack<>();
        Set<Node2> visitedStates = new HashSet<>();

        Node2 initialState = new Node2(3, 3, 1, null);
        stack.push(initialState);

        while (!stack.isEmpty()) {
            Node2 current = stack.pop();

            // Print the current state when it is added to the stack
            // System.out.println("(" + current.missionaries + ", " + current.cannibals + ",
            // " + current.boat + ")");

            if (current.isGoal()) {
                System.out.println("Goal state reached!");
                printSolution(current);
                return;
            }

            visitedStates.add(current);

            List<Node2> nextStates = current.getNextStates();
            for (Node2 nextState : nextStates) {
                if (!visitedStates.contains(nextState)) {
                    stack.push(nextState);
                }
            }
        }

        System.out.println("No solution found.");
    }

    public static void printSolution(Node2 goalNode) {
        List<Node2> path = new ArrayList<>();

        // Build the path from the initial state to the goal state
        while (goalNode != null) {
            path.add(goalNode);
            goalNode = goalNode.parent;
        }

        // Print the states in the correct order
        for (int i = path.size() - 1; i >= 0; i--) {
            Node2 node = path.get(i);
            System.out.println("(" + node.missionaries + ", " + node.cannibals + ", " + node.boat + ")");
        }
    }
}






//Eight Puzzle bfs
import java.util.*;

public class EightPuzzlebfs {

    public static void main(String args[]) {
        String[][] a;
        String[][] goal;
        int i, j, rows, columns;
        rows = columns = 3;
        Scanner sc = new Scanner(System.in);
        a = new String[rows][columns];
        goal = new String[rows][columns];
        System.out.println("Please input the elements for initial state :");

        for (i = 0; i < a.length; i++) {
            for (j = 0; j < a.length; j++) {
                a[i][j] = sc.nextLine();
                if (a[i][j].length() != 1 || (a[i][j].charAt(0) < '1' && a[i][j].charAt(0) != ' ')
                        || a[i][j].charAt(0) > '8') {
                    System.out.println(
                            "Error: Input should be any number between 1 to 8 or a single space\nProgram Terminated");
                    return;
                }
            }
        }

        System.out.println("Please input the Goal state:");

        for (i = 0; i < goal.length; i++) {
            for (j = 0; j < goal.length; j++) {
                goal[i][j] = sc.nextLine();
                if (goal[i][j].length() != 1 || (goal[i][j].charAt(0) < '1' && goal[i][j].charAt(0) != ' ')
                        || goal[i][j].charAt(0) > '8') {
                    System.out.println(
                            "Error: Input should be any number between 1 to 8 or a single space\nProgram Terminated");
                    return;
                }
            }
        }

        // Check if the initial state is solvable
        int[][] initialState = new int[rows][columns];
        for (i = 0; i < rows; i++) {
            for (j = 0; j < columns; j++) {
                initialState[i][j] = a[i][j].equals(" ") ? 0 : Integer.parseInt(a[i][j]);
            }
        }

        if (!isSolvable(initialState)) {
            System.out.println("This puzzle is not solvable");
            return;
        }

        State state = new State(a, 0);
        new Solution(state, goal);
    }

    // Function to check if a state is solvable
    private static boolean isSolvable(int[][] board) {
        int[] arr = new int[9];
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                arr[k++] = board[i][j];
            }
        }
        int invCount = getInversions(arr);
        return (invCount % 2 == 0);
    }

    private static int getInversions(int[] arr) {
        int invCount = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j] && arr[i] != 0 && arr[j] != 0) {
                    invCount++;
                }
            }
        }
        return invCount;
    }
}

class State {
    String[][] blocks;
    int depth;

    State(String[][] blocks, int depth) {
        this.blocks = new String[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(blocks[i], 0, this.blocks[i], 0, 3);
        }
        this.depth = depth;
    }

    boolean isGoal(String[][] goal) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!blocks[i][j].equals(goal[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        State state = (State) o;
        return Arrays.deepEquals(blocks, state.blocks);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(blocks);
    }
}

class Solution {
    static List<State> expanded;

    Solution(State initial, String[][] goal) {
        expanded = new ArrayList<>();
        bfs(initial, goal);
    }

    void bfs(State initial, String[][] goal) {
        Queue<State> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();
        queue.add(initial);
        visited.add(initial);

        while (!queue.isEmpty()) {
            State state = queue.poll();
            expanded.add(state);

            if (state.isGoal(goal)) {
                printSolution(state);
                return;
            }

            for (State neighbor : getNeighbors(state)) {
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        System.out.println("No solution found.");
    }

    List<State> getNeighbors(State state) {
        List<State> neighbors = new ArrayList<>();
        int x = 0, y = 0;
        outer: for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state.blocks[i][j].equals(" ")) {
                    x = i;
                    y = j;
                    break outer;
                }
            }
        }

        int[][] directions = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
        for (int[] direction : directions) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            if (isValid(newX, newY)) {
                String[][] newBlocks = new String[3][3];
                for (int i = 0; i < 3; i++) {
                    System.arraycopy(state.blocks[i], 0, newBlocks[i], 0, 3);
                }
                newBlocks[x][y] = newBlocks[newX][newY];
                newBlocks[newX][newY] = " ";
                neighbors.add(new State(newBlocks, state.depth + 1));
            }
        }
        return neighbors;
    }

    boolean isValid(int x, int y) {
        return x >= 0 && x < 3 && y >= 0 && y < 3;
    }

    void printSolution(State state) {
        for (State s : expanded) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    System.out.print(s.blocks[i][j] + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}





//Eight Puzzle dfs
import java.util.*;

public class EightPuzzledfs {

    public static void main(String args[]) {
        String[][] a;
        String[][] goal;
        int i, j, rows, columns;
        rows = columns = 3;
        Scanner sc = new Scanner(System.in);
        a = new String[rows][columns];
        goal = new String[rows][columns];
        System.out.println("Please input the elements for initial state :");

        for (i = 0; i < a.length; i++) {
            for (j = 0; j < a.length; j++) {
                a[i][j] = sc.nextLine();
                if (a[i][j].length() != 1 || (a[i][j].charAt(0) < '1' && a[i][j].charAt(0) != ' ')
                        || a[i][j].charAt(0) > '8') {
                    System.out.println(
                            "Error: Input should be any number between 1 to 8 or a single space\nProgram Terminated");
                    return;
                }
            }
        }

        System.out.println("Please input the Goal state:");

        for (i = 0; i < goal.length; i++) {
            for (j = 0; j < goal.length; j++) {
                goal[i][j] = sc.nextLine();
                if (goal[i][j].length() != 1 || (goal[i][j].charAt(0) < '1' && goal[i][j].charAt(0) != ' ')
                        || goal[i][j].charAt(0) > '8') {
                    System.out.println(
                            "Error: Input should be any number between 1 to 8 or a single space\nProgram Terminated");
                    return;
                }
            }
        }

        // Check if the initial state is solvable
        int[][] initialState = new int[rows][columns];
        for (i = 0; i < rows; i++) {
            for (j = 0; j < columns; j++) {
                initialState[i][j] = a[i][j].equals(" ") ? 0 : Integer.parseInt(a[i][j]);
            }
        }

        if (!isSolvable(initialState)) {
            System.out.println("This puzzle is not solvable");
            return;
        }

        State state = new State(a, 0);
        new Solution(state, goal);
    }

    // Function to check if a state is solvable
    private static boolean isSolvable(int[][] board) {
        int[] arr = new int[9];
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                arr[k++] = board[i][j];
            }
        }
        int invCount = getInversions(arr);
        return (invCount % 2 == 0);
    }

    private static int getInversions(int[] arr) {
        int invCount = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] > arr[j] && arr[i] != 0 && arr[j] != 0) {
                    invCount++;
                }
            }
        }
        return invCount;
    }
}

class State {
    String[][] blocks;
    int depth;

    State(String[][] blocks, int depth) {
        this.blocks = new String[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(blocks[i], 0, this.blocks[i], 0, 3);
        }
        this.depth = depth;
    }

    boolean isGoal(String[][] goal) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!blocks[i][j].equals(goal[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        State state = (State) o;
        return Arrays.deepEquals(blocks, state.blocks);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(blocks);
    }
}

class Solution {
    static List<State> expanded;
    static final int MAX_DEPTH = 20; // Set an appropriate depth limit

    Solution(State initial, String[][] goal) {
        expanded = new ArrayList<>();
        dfs(initial, goal, new HashSet<>(), 0);
    }

    void dfs(State state, String[][] goal, Set<State> visited, int depth) {
        if (depth > MAX_DEPTH) {
            return;
        }
        expanded.add(state);
        visited.add(state);

        if (state.isGoal(goal)) {
            printSolution();
            return;
        }

        for (State neighbor : getNeighbors(state)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, goal, visited, depth + 1);
            }
        }
    }

    List<State> getNeighbors(State state) {
        List<State> neighbors = new ArrayList<>();
        int x = 0, y = 0;
        outer: for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state.blocks[i][j].equals(" ")) {
                    x = i;
                    y = j;
                    break outer;
                }
            }
        }

        int[][] directions = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };
        for (int[] direction : directions) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            if (isValid(newX, newY)) {
                String[][] newBlocks = new String[3][3];
                for (int i = 0; i < 3; i++) {
                    System.arraycopy(state.blocks[i], 0, newBlocks[i], 0, 3);
                }
                newBlocks[x][y] = newBlocks[newX][newY];
                newBlocks[newX][newY] = " ";
                neighbors.add(new State(newBlocks, state.depth + 1));
            }
        }
        return neighbors;
    }

    boolean isValid(int x, int y) {
        return x >= 0 && x < 3 && y >= 0 && y < 3;
    }

    void printSolution() {
        for (State s : expanded) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    System.out.print(s.blocks[i][j] + "\t");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}






//Eight Puzzle using Best First Search
import java.util.*;

class PuzzleState implements Comparable<PuzzleState> {
    int[][] board;
    int emptyRow, emptyCol;
    int gCost, hCost;
    PuzzleState parent;

    public PuzzleState(int[][] board, int gCost, PuzzleState parent) {
        this.board = board;
        this.gCost = gCost;
        this.parent = parent;
        this.hCost = calculateHeuristic();
        findEmptyTile();
    }

    private void findEmptyTile() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                    return;
                }
            }
        }
    }

    private int calculateHeuristic() {
        int h = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int value = board[i][j];
                if (value != 0) {
                    int targetRow = (value - 1) / board.length;
                    int targetCol = (value - 1) % board[i].length;
                    h += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
        }
        return h;
    }

    @Override
    public int compareTo(PuzzleState other) {
        return (this.gCost + this.hCost) - (other.gCost + other.hCost);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PuzzleState))
            return false;
        PuzzleState other = (PuzzleState) obj;
        return Arrays.deepEquals(this.board, other.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public List<PuzzleState> getSuccessors() {
        List<PuzzleState> successors = new ArrayList<>();
        int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        for (int[] direction : directions) {
            int newRow = emptyRow + direction[0];
            int newCol = emptyCol + direction[1];
            if (newRow >= 0 && newRow < board.length && newCol >= 0 && newCol < board[0].length) {
                int[][] newBoard = deepCopy(board);
                newBoard[emptyRow][emptyCol] = newBoard[newRow][newCol];
                newBoard[newRow][newCol] = 0;
                successors.add(new PuzzleState(newBoard, gCost + 1, this));
            }
        }
        return successors;
    }

    private int[][] deepCopy(int[][] original) {
        if (original == null) {
            return null;
        }
        int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }

    public void printState() {
        for (int[] row : board) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }
}

public class EightPuzzleBestFirstSearch {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the initial state (3x3 grid, use 0 for the empty tile):");
        int[][] initialBoard = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                initialBoard[i][j] = scanner.nextInt();
            }
        }

        System.out.println("Enter the goal state (3x3 grid, use 0 for the empty tile):");
        int[][] goalBoard = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                goalBoard[i][j] = scanner.nextInt();
            }
        }

        if (!isSolvable(initialBoard)) {
            System.out.println("Not solvable");
            return;
        }

        PuzzleState initialState = new PuzzleState(initialBoard, 0, null);
        PuzzleState goalState = new PuzzleState(goalBoard, 0, null);

        PriorityQueue<PuzzleState> openSet = new PriorityQueue<>();
        Set<PuzzleState> closedSet = new HashSet<>();

        openSet.add(initialState);

        while (!openSet.isEmpty()) {
            PuzzleState current = openSet.poll();

            if (current.equals(goalState)) {
                printSolutionPath(current);
                return;
            }

            closedSet.add(current);

            for (PuzzleState successor : current.getSuccessors()) {
                if (!closedSet.contains(successor) && !openSet.contains(successor)) {
                    openSet.add(successor);
                }
            }
        }

        System.out.println("No solution found.");
    }

    private static void printSolutionPath(PuzzleState state) {
        if (state == null)
            return;
        printSolutionPath(state.parent);
        state.printState();
    }

    private static boolean isSolvable(int[][] board) {
        int[] oneDArray = new int[board.length * board.length];
        int k = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                oneDArray[k++] = board[i][j];
            }
        }
        int inversions = 0;
        for (int i = 0; i < oneDArray.length; i++) {
            if (oneDArray[i] == 0)
                continue;
            for (int j = i + 1; j < oneDArray.length; j++) {
                if (oneDArray[j] == 0)
                    continue;
                if (oneDArray[i] > oneDArray[j])
                    inversions++;
            }
        }
        return inversions % 2 == 0;
    }
}







//Eight Puzzle using A Star
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Solution {

    public static PriorityQueue<State> pq = new PriorityQueue<>();
    public static ArrayList<State> expanded = new ArrayList<>();
    public static String[][] goal;

    public Solution(State first) {
        if (first == null) {
            System.out.println("Please provide an input");
        }
        pq.add(first);
        ArrayList<State> list = new ArrayList<>();
        while (!pq.isEmpty()) {
            State current = pq.poll();
            expanded.add(current);
            if (Arrays.deepEquals(current.blocks, goal)) {
                break;
            }
            list = current.expand(current);
            for (State l : list) {
                boolean visited = false;
                for (State e : expanded) {
                    if (Arrays.deepEquals(l.blocks, e.blocks)) {
                        visited = true;
                        break;
                    }
                }
                if (visited)
                    continue;
                pq.add(l);
            }
        }
    }

    public static void main(String args[]) {
        String a[][];
        int i, j, rows, columns;
        rows = columns = 3;
        Scanner sc = new Scanner(System.in);
        a = new String[rows][columns];
        goal = new String[rows][columns];
        System.out.println("Please input the elements for initial state :");

        for (i = 0; i < a.length; i++) {
            for (j = 0; j < a.length; j++) {
                a[i][j] = sc.nextLine();
                if (a[i][j].length() != 1 || (a[i][j].charAt(0) < '1' && a[i][j].charAt(0) != ' ')
                        || a[i][j].charAt(0) > '8') {
                    System.out.println(
                            "Error: Input should be any number between 1 to 8 or a single space\nProgram Terminated");
                    return;
                }
            }
        }
        System.out.println("Please input the Goal state:");

        for (i = 0; i < goal.length; i++) {
            for (j = 0; j < goal.length; j++) {
                goal[i][j] = sc.nextLine();
                if (goal[i][j].length() != 1 || (goal[i][j].charAt(0) < '1' && goal[i][j].charAt(0) != ' ')
                        || goal[i][j].charAt(0) > '8') {
                    System.out.println(
                            "Error: Input should be any number between 1 to 8 or a single space\nProgram Terminated");
                    return;
                }
            }
        }

        // Check if the initial state is solvable
        int[][] initialState = new int[rows][columns];
        for (i = 0; i < rows; i++) {
            for (j = 0; j < columns; j++) {
                initialState[i][j] = a[i][j].equals(" ") ? 0 : Integer.parseInt(a[i][j]);
            }
        }

        if (!isSolvable(initialState)) {
            System.out.println("This puzzle is not solvable");
            return;
        }

        State state = new State(a, 0);
        new Solution(state);
        for (State states : expanded) {
            for (int l = 0; l < 3; l++) {
                for (int m = 0; m < 3; m++) {
                    System.out.print(states.blocks[l][m] + "\t");
                }
                System.out.println();
            }
            System.out.println("f(n) :" + states.f);
            System.out.println("h(n) :" + (states.f - states.level));
            System.out.println("g(n) :" + (states.level));
            System.out.println('\n');
        }
        System.out.println("Total Nodes expanded :" + expanded.size());
        System.out.println("Total Nodes generated:" + (expanded.size() + pq.size()));
    }

    static int getInvCount(int[] arr) {
        int inv_count = 0;
        for (int i = 0; i < 9; i++)
            for (int j = i + 1; j < 9; j++)
                if (arr[i] > 0 && arr[j] > 0 && arr[i] > arr[j])
                    inv_count++;
        return inv_count;
    }

    static boolean isSolvable(int[][] puzzle) {
        int linearPuzzle[] = new int[9];
        int k = 0;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                linearPuzzle[k++] = puzzle[i][j];
        int invCount = getInvCount(linearPuzzle);
        return (invCount % 2 == 0);
    }
}

class State implements Comparable<State> {
    public int f;
    public String[][] blocks;
    public int level;

    public State(String[][] a, int level) {
        int N = a.length;
        this.blocks = new String[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.blocks[i][j] = a[i][j];
            }
        }
        this.level = level;
        this.f = manhattan() + level;
    }

    private int manhattan() {
        int sum = 0;
        int[] index = new int[2];
        int N = Solution.goal.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (this.blocks[i][j].trim().isEmpty()) {
                    continue;
                }
                index = find_index(Integer.parseInt(this.blocks[i][j]));
                sum = sum + (Math.abs(i - index[0]) + Math.abs(j - index[1]));
            }
        }
        return sum;
    }

    private int[] find_index(int a) {
        int[] index = new int[2];
        int N = Solution.goal.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (Solution.goal[i][j].trim().isEmpty()) {
                    continue;
                }
                if (Solution.goal[i][j].trim().equals(String.valueOf(a))) {
                    index[0] = i;
                    index[1] = j;
                    return index;
                }
            }
        }
        return index;
    }

    public ArrayList<State> expand(State parent) {
        ArrayList<State> successor = new ArrayList<>();
        int N = this.blocks.length;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (parent.blocks[i][j].trim().isEmpty()) {
                    if (i - 1 >= 0) {
                        String[][] a = new String[N][N];
                        for (int l = 0; l < N; l++) {
                            for (int m = 0; m < N; m++) {
                                a[l][m] = parent.blocks[l][m];
                            }
                        }
                        a = swap(a, i, j, i - 1, j);
                        State b = new State(a, parent.level + 1);
                        successor.add(b);
                    }
                    if (j - 1 >= 0) {
                        String[][] a = new String[N][N];
                        for (int l = 0; l < N; l++) {
                            for (int m = 0; m < N; m++) {
                                a[l][m] = parent.blocks[l][m];
                            }
                        }
                        a = swap(a, i, j, i, j - 1);
                        State b = new State(a, parent.level + 1);
                        successor.add(b);
                    }
                    if (i + 1 < N) {
                        String[][] a = new String[N][N];
                        for (int l = 0; l < N; l++) {
                            for (int m = 0; m < N; m++) {
                                a[l][m] = parent.blocks[l][m];
                            }
                        }
                        a = swap(a, i, j, i + 1, j);
                        State b = new State(a, parent.level + 1);
                        successor.add(b);
                    }
                    if (j + 1 < N) {
                        String[][] a = new String[N][N];
                        for (int l = 0; l < N; l++) {
                            for (int m = 0; m < N; m++) {
                                a[l][m] = parent.blocks[l][m];
                            }
                        }
                        a = swap(a, i, j, i, j + 1);
                        State b = new State(a, parent.level + 1);
                        successor.add(b);
                    }
                }
            }
        }
        return successor;
    }

    private String[][] swap(String[][] a, int row1, int col1, int row2, int col2) {
        String[][] copy = new String[a.length][a[0].length];
        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a[i], 0, copy[i], 0, a[i].length);
        }
        String tmp = copy[row1][col1];
        copy[row1][col1] = copy[row2][col2];
        copy[row2][col2] = tmp;
        return copy;
    }

    @Override
    public int compareTo(State o) {
        if (this.f == o.f) {
            return this.manhattan() - o.manhattan();
        }
        return this.f - o.f;
    }
}





//TSP Hill Climbing 

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HillClimbingTSP {
    private static int[][] distanceMatrix;

    private static int totalDistance(List<Integer> path) {
        // Calculate the total distance traveled in the given path
        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += distanceMatrix[path.get(i)][path.get(i + 1)];
        }
        total += distanceMatrix[path.get(path.size() - 1)][path.get(0)]; // Return to starting city
        return total;
    }

    private static List<Integer> generateRandomPath(int numCities) {
        List<Integer> path = new ArrayList<>();
        for (int i = 0; i < numCities; i++) {
            path.add(i);
        }
        Random rand = new Random();
        for (int i = numCities - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = path.get(i);
            path.set(i, path.get(j));
            path.set(j, temp);
        }
        return path;
    }

    public static void hillClimbingTSP(int numCities, int maxIterations) {
        List<Integer> currentPath = generateRandomPath(numCities); // Initial solution
        int currentDistance = totalDistance(currentPath);
        for (int it = 0; it < maxIterations; it++) {
            // Generate a neighboring solution by swapping two random cities
            List<Integer> neighborPath = new ArrayList<>(currentPath);
            int i = new Random().nextInt(numCities);
            int j = new Random().nextInt(numCities);
            int temp = neighborPath.get(i);
            neighborPath.set(i, neighborPath.get(j));
            neighborPath.set(j, temp);
            int neighborDistance = totalDistance(neighborPath);
            // If the neighbor solution is better, move to it
            if (neighborDistance < currentDistance) {
                currentPath = neighborPath;
                currentDistance = neighborDistance;
            }
        }
        System.out.print("Optimal path: ");
        for (int city : currentPath) {
            System.out.print(city + " ");
        }
        System.out.println();
        System.out.println("Total distance: " + currentDistance);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of cities: ");
        int numCities = scanner.nextInt();

        System.out.print("Enter the maximum number of iterations: ");
        int maxIterations = scanner.nextInt();

        // Initialize distance matrix
        distanceMatrix = new int[numCities][numCities];
        System.out.println("Enter the distance matrix:");
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                distanceMatrix[i][j] = scanner.nextInt();
            }
        }

        hillClimbingTSP(numCities, maxIterations);

        scanner.close();
    }
}






//N Queen Hill Climbing Min Conflicts

/* This class solve the N Queens problem by both Hill Climbing and Min-Conflict methods.
 1. The n-board is represented as an array of n elements which store the row number there the queen is placed.
 2. Everytime when there is no solution found, a random board is generated.
 3. Iterations need to be mentioned for Min-Conflicts algorithm*/
/****************************************************/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class NQueenProblem {
	// This method calculates all the row conflicts for a queen placed in a
	// particular cell.
	public static int rowCollisions(int a[], int n) {
		int collisions = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					if (a[i] == a[j]) // Calculates whether the queen is in the same row.
						collisions++;
				}
			}
		}
		return collisions;
	}

	// This method calculates all the diagonal conflicts for a particular position
	// of the queen
	public static int diagonalCollisions(int a[], int n) {
		int collisions = 0, d = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					d = Math.abs(i - j);
					if ((a[i] == a[j] + d) || (a[i] == a[j] - d)) // This verifies whether there are any diagonal
																	// collisions
						collisions++;
				}
			}
		}
		return collisions;
	}

	// This method returns total number of collisions for a particular queen
	// position
	public static int totalCollisions(int a[], int n) {
		int collisions = 0;
		collisions = rowCollisions(a, n) + diagonalCollisions(a, n);
		return collisions;
	}

	/*
	 * This method calculates the conflicts for the current state of the board and
	 * quits whenever finds a better state.
	 * Note: This function is used for Hill Climbing algorithm
	 */
	public static boolean bestSolution(int a[], int n) {
		int min, collisions = 0, row = -1, col = -1, m;
		boolean checkBetter = false;
		int[] best;
		// Sets min variable to the collisions of current board so that if finds better
		// than this it will quit.
		min = totalCollisions(a, n);
		best = Arrays.copyOf(a, n); // Create a duplicate array for handling different operations
		for (int i = 0; i < n; i++) // This iteration is for each column
		{
			if (checkBetter) // If it finds and better state than the current, it will quit
				break;
			m = best[i];
			for (int j = 0; j < n; j++) // This iteration is for each row in the selected column
			{
				if (j != m) // This condition ensures that, current queen position is not taken into
							// consideration.
				{
					best[i] = j; // Assigning the queen to each position and then calculating the collisions
					collisions = totalCollisions(best, n);
					if (min > collisions) // If a better state is found, that particular column and row values are
											// stored
					{
						col = i;
						row = j;
						min = collisions;
						checkBetter = true;
						break;
					}
				}
				best[i] = m; // Restoring the array to the current board position
			}
		}
		if (col == -1 || row == -1) // If there is no better state found
		{
			System.out.println("Reached Local Maxima with " + collisions + " Regenerating randomly");
			return false;
		}
		a[col] = row;
		return true; // Returns true to the main function if there is any better state found
	}

	// Below function generates a random state of the board
	public static void randomGenerate(int[] a, int n) {
		Random gen = new Random();
		for (int i = 0; i < n; i++)
			a[i] = gen.nextInt(n) + 0;
	}

	// Below function verifies whether the current state of the board is the
	// solution(I.e with zero conflicts)
	public static boolean isSolution(int a[], int n) {
		if (totalCollisions(a, n) == 0)
			return true;
		return false;
	}

	// Below method finds the solution for the n-queens problem with Min-Conflicts
	// algorithm
	public static void minConflict(int b[], int n, int iterations) {
		// This array list is for storing the columns from which a random column will be
		// selected
		ArrayList<Integer> store = new ArrayList<Integer>();
		fillList(store, n);
		Random gen = new Random();
		int randomSelection, currentValue, randomValue;
		int col, randomCount = 0, movesTotal = 0, movesSolution = 0, row = 0, maxSteps;
		maxSteps = iterations; // The maximum steps that can be allowed to find a solution with this algorithm
		while (!isSolution(b, n)) // Loops until it finds a solution
		{
			randomSelection = gen.nextInt(store.size()) + 0; // Randomly selects a column from the available
			currentValue = b[store.get(randomSelection)]; // This stores the current queue position in the randomly
															// selected column
			randomValue = store.get(randomSelection);
			int min = collisionsMinConflict(b, n, randomValue);// Sets the minimum variable to the current queue
																// collisions
			int min_compare = min;
			store.remove(randomSelection);
			for (int i = 0; i < n; i++) {
				if (currentValue != i) {
					b[randomValue] = i;
					col = collisionsMinConflict(b, n, randomValue); // Calculates the collisions of the queen at
																	// particular position
					if (col < min) {
						min = col;
						row = i;
					}
				}
			}
			if (min_compare == min) // When there is no queen with minimum conflicts than the current position
			{
				if (maxSteps != 0) // Checks if the maximum steps is reached
				{
					if (store.size() > 0) // checks whether there are columns available in the Array List
					{
						b[randomValue] = currentValue; // restores the queen back to the previous position
						maxSteps--;
					} else {
						fillList(store, n);
					}
				} else // If the max steps is reached then, the board is regenerated and initiated the
						// max steps variable
				{
					randomCount++;
					movesSolution = 0;
					randomGenerate(b, n);
					fillList(store, n);
					maxSteps = iterations;
				}
			} else // When we find the the position in the column with minimum conflicts
			{
				movesTotal++;
				movesSolution++;
				b[randomValue] = row;
				min_compare = min;
				store.clear();
				maxSteps--;
				fillList(store, n);
			}
		}
		System.out.println();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (j == b[i])
					System.out.print("Q ");
				else
					System.out.print("X ");
			}
			System.out.println();
		}
		System.out.println("Total number of Random Restarts: " + randomCount);
		System.out.println("Total number of Moves: " + movesTotal);
		System.out.println("Number of Moves in the solution set: " + movesSolution);
	}

	// Below function returns the collisions of a queen in a particular column of
	// the board
	public static int collisionsMinConflict(int[] b, int n, int index) {
		int collisions = 0, t = 0;
		for (int i = 0; i < n; i++) {
			if (i != index) {
				t = Math.abs(index - i);
				if (b[i] == b[index])
					collisions++;
				else if (b[index] == b[i] + t || b[index] == b[i] - t)
					collisions++;
			}
		}
		return collisions;
	}

	// Below function fills the Array List with numbers 0 to n-1
	public static void fillList(ArrayList<Integer> store, int n) {
		for (int i = 0; i < n; i++) {
			store.add(i);
		}
		return;
	}

	// Main function
	public static void main(String[] args) {
		int a[], b[];
		int n, totalRestart = 0, movesTotal = 0, movesSolution = 0, choice;
		System.out.println("Please enter the value of n:");
		Scanner sc = new Scanner(System.in);
		n = sc.nextInt();
		if ((n > 1 && n < 4) || n < 1) {
			System.out.println("*Please choose n value either greater than 3 or equals to 1 - Program Terminated");
			return;
		}
		if (n == 1) {
			System.out.println("There is no choice of algorithm for this value of 'n':");
			System.out.println("Q");
			return;
		}
		System.out.println("Please select one from the below options:");
		System.out.println("1. Solve n queens with Hill Climbing and Random Restart");
		System.out.println("2. Min Conflict method with random restart");
		System.out.println("3. Both methods and their results");
		choice = sc.nextInt();
		if (choice < 1 || choice > 3) {
			System.out.println("*Program terminated - Wrong option selected");
			return;
		}
		a = new int[n];
		b = new int[n];
		randomGenerate(a, n); // Randomly generate the board
		b = Arrays.copyOf(a, n);
		// The below code will be executed if the user chooses he options 1 or 3(n-queen
		// with Hill Climbing method)
		if (choice == 1 || choice == 3) {
			System.out.println("**********Hill Climbing with Random Restart*********");
			long startTime = System.currentTimeMillis();
			while (!isSolution(a, n)) // Executes until a solution is found
			{
				if (bestSolution(a, n)) // If a better state for a board is found
				{
					movesTotal++;
					movesSolution++;
					continue;
				} else // If a better state is not found
				{
					movesSolution = 0;
					randomGenerate(a, n); // Board is generated Randomly
					totalRestart++;
				}
			}
			long endTime = System.currentTimeMillis();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (j == a[i])
						System.out.print("Q ");
					else
						System.out.print("X ");
				}
				System.out.println();
			}
			System.out.println("Number of Restarts: " + totalRestart);
			System.out.println("Total number of moves taken: " + movesTotal); // Gives the total number of moves from
																				// starting point
			System.out.println("Number of moves in the solution set: " + movesSolution); // Gives number of steps in the
																							// solution set.
			System.out.println("Time Taken in milli seconds: " + (endTime - startTime));
		}
		// If the Min-Conflict algorithm is selected
		if (choice == 2 || choice == 3) {
			int iterations = 0;
			System.out.println();
			System.out.println("*******Min Conflict With Random Restart*******");
			System.out.println("Please enter the maximum number of steps for iteration:");
			iterations = sc.nextInt();
			sc.close();
			long startTime = System.currentTimeMillis();
			minConflict(b, n, iterations);
			long endTime = System.currentTimeMillis();
			System.out.println("Time Taken in milli seconds: " + (endTime - startTime));
		}
	}

}






//Sudoku 
#include <iostream>

using namespace std;

#define N 9

void print(int arr[N][N])
{
    for (int i = 0; i < N; i++)
    {
        for (int j = 0; j < N; j++)
            cout << arr[i][j] << " ";
        cout << endl;
    }
}

bool isSafe(int grid[N][N], int row,
            int col, int num)
{

    for (int x = 0; x <= 8; x++)
        if (grid[row][x] == num)
            return false;

    for (int x = 0; x <= 8; x++)
        if (grid[x][col] == num)
            return false;

    int startRow = row - row % 3,
        startCol = col - col % 3;

    for (int i = 0; i < 3; i++)
        for (int j = 0; j < 3; j++)
            if (grid[i + startRow][j +
                                   startCol] == num)
                return false;

    return true;
}

bool solveSudoku(int grid[N][N], int row, int col)
{

    if (row == N - 1 && col == N)
        return true;

    if (col == N)
    {
        row++;
        col = 0;
    }

    if (grid[row][col] > 0)
        return solveSudoku(grid, row, col + 1);

    for (int num = 1; num <= N; num++)
    {

        if (isSafe(grid, row, col, num))
        {

            grid[row][col] = num;

            if (solveSudoku(grid, row, col + 1))
                return true;
        }

        grid[row][col] = 0;
    }
    return false;
}

int main()
{

    int grid[N][N] = {{3, 0, 6, 5, 0, 8, 4, 0, 0},
                      {5, 2, 0, 0, 0, 0, 0, 0, 0},
                      {0, 8, 7, 0, 0, 0, 0, 3, 1},
                      {0, 0, 3, 0, 1, 0, 0, 8, 0},
                      {9, 0, 0, 8, 6, 3, 0, 0, 5},
                      {0, 5, 0, 0, 9, 0, 6, 0, 0},
                      {1, 3, 0, 0, 0, 0, 2, 5, 0},
                      {0, 0, 0, 0, 0, 0, 0, 7, 4},
                      {0, 0, 5, 2, 0, 6, 3, 0, 0}};

    if (solveSudoku(grid, 0, 0))
        print(grid);
    else
        cout << "no solution exists " << endl;

    return 0;
}





// N Queen Backtracking
import java.util.*;

class NQueenBacktracking {
    // ld is an array where its indices indicate row-col+N-1
    // (N-1) is for shifting the difference to store negative indices
    static int[] ld;

    // rd is an array where its indices indicate row+col
    // and used to check whether a queen can be placed on right diagonal or not
    static int[] rd;

    // Column array where its indices indicates column and
    // used to check whether a queen can be placed in that row or not
    static int[] cl;

    // Dimension of the board
    static int N;

    // A utility function to print solution
    static void printSolution(int board[][]) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (board[i][j] == 1) {
                    System.out.print("Q ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
    }

    // A recursive utility function to solve N Queen problem
    static boolean solveNQUtil(int board[][], int col) {
        // Base case: If all queens are placed then return true
        if (col >= N)
            return true;

        // Consider this column and try placing this queen in all rows one by one
        for (int i = 0; i < N; i++) {
            // Check if the queen can be placed on board[i][col]
            if ((ld[i - col + N - 1] != 1 && rd[i + col] != 1) && cl[i] != 1) {
                // Place this queen in board[i][col]
                board[i][col] = 1;
                ld[i - col + N - 1] = rd[i + col] = cl[i] = 1;

                // Recur to place rest of the queens
                if (solveNQUtil(board, col + 1))
                    return true;

                // If placing queen in board[i][col] doesn't lead to a solution, then
                // remove queen from board[i][col]
                board[i][col] = 0; // BACKTRACK
                ld[i - col + N - 1] = rd[i + col] = cl[i] = 0;
            }
        }

        // If the queen cannot be placed in any row in this column col then return false
        return false;
    }

    // This function solves the N Queen problem using Backtracking.
    // It mainly uses solveNQUtil() to solve the problem.
    static boolean solveNQ() {
        int[][] board = new int[N][N];
        for (int i = 0; i < N; i++) {
            Arrays.fill(board[i], 0);
        }

        ld = new int[2 * N - 1];
        rd = new int[2 * N - 1];
        cl = new int[N];

        if (!solveNQUtil(board, 0)) {
            System.out.printf("Solution does not exist");
            return false;
        }

        printSolution(board);
        return true;
    }

    // Driver Code
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the value of N: ");
        N = scanner.nextInt();

        solveNQ();
    }
}

