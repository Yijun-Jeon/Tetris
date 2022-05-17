import java.awt.Color;

public class TetrisModel {

	public static final int BLOCKSIZE = 20;
	public static final int BLOCKS[][][][] = {
	         {  //¡á
	            //¡á¡á¡á
	            {{0,0,0,0},{1,0,0,0},{1,1,1,0},{0,0,0,0}},
	            {{0,1,1,0},{0,1,0,0},{0,1,0,0},{0,0,0,0}},
	            {{1,1,1,0},{0,0,1,0},{0,0,0,0},{0,0,0,0}},
	            {{0,0,1,0},{0,0,1,0},{0,1,1,0},{0,0,0,0}}},
	         {  //  ¡á
	            //¡á¡á¡á
	            {{0,0,0,0},{0,0,1,0},{1,1,1,0},{0,0,0,0}},
	            {{0,1,0,0},{0,1,0,0},{0,1,1,0},{0,0,0,0}},
	            {{0,0,0,0},{1,1,1,0},{1,0,0,0},{0,0,0,0}},
	            {{0,1,1,0},{0,0,1,0},{0,0,1,0},{0,0,0,0}}},
	         {  //¡á¡á
	            //¡á¡á
	            {{0,0,0,0},{1,1,0,0},{1,1,0,0},{0,0,0,0}},
	            {{0,0,0,0},{1,1,0,0},{1,1,0,0},{0,0,0,0}},
	            {{0,0,0,0},{1,1,0,0},{1,1,0,0},{0,0,0,0}},
	            {{0,0,0,0},{1,1,0,0},{1,1,0,0},{0,0,0,0}}},
	         {  //¡á¡á¡á¡á
	            {{0,0,0,0},{0,0,0,0},{1,1,1,1},{0,0,0,0}},
	            {{0,1,0,0},{0,1,0,0},{0,1,0,0},{0,1,0,0}},
	            {{0,0,0,0},{0,0,0,0},{1,1,1,1},{0,0,0,0}},
	            {{0,1,0,0},{0,1,0,0},{0,1,0,0},{0,1,0,0}}},
	         {  // ¡á
	            //¡á¡á¡á
	            {{0,0,0,0},{0,1,0,0},{1,1,1,0},{0,0,0,0}},
	            {{0,1,0,0},{0,1,1,0},{0,1,0,0},{0,0,0,0}},
	            {{0,0,0,0},{1,1,1,0},{0,1,0,0},{0,0,0,0}},
	            {{0,1,0,0},{1,1,0,0},{0,1,0,0},{0,0,0,0}}},
	         {  //¡á¡á
	            // ¡á¡á
	            {{0,0,0,0},{1,1,0,0},{0,1,1,0},{0,0,0,0}},
	            {{0,0,1,0},{0,1,1,0},{0,1,0,0},{0,0,0,0}},
	            {{0,0,0,0},{1,1,0,0},{0,1,1,0},{0,0,0,0}},
	            {{0,0,1,0},{0,1,1,0},{0,1,0,0},{0,0,0,0}}},
	         {  // ¡á¡á
	            //¡á¡á
	            {{0,0,0,0},{0,1,1,0},{1,1,0,0},{0,0,0,0}},
	            {{0,1,0,0},{0,1,1,0},{0,0,1,0},{0,0,0,0}},
	            {{0,0,0,0},{0,1,1,0},{1,1,0,0},{0,0,0,0}},
	            {{0,1,0,0},{0,1,1,0},{0,0,1,0},{0,0,0,0}}}
	   };
	public static final int[][] GAMEBOARD = {
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
	
	public static final Color[] COLOR = {
		Color.red, Color.blue, Color.yellow, Color.orange, Color.cyan, Color.magenta, Color.green,
		
	};
}