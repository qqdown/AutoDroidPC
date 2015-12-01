package edu.nju.autodroid.utils;

public class Algorithm {
	
	//编辑距离算法，返回两个数组之间的编辑距离
	public static <T> int EditDistance(T[] arr1, T[] arr2){ 
		int n = arr1.length;
		int m = arr2.length;
		if(n==0)
			return m;
		if(m==0)
			return n;
		
		int[][] matrix = new int[n+1][m+1];
		for(int i=0; i<=n; i++)
		{
			matrix[i][0] = i;
		}
		for(int j=0; j<=m; j++)
		{
			matrix[0][j] = j;
		}
		
		for(int i=1; i<=n; i++){
			for(int j=1; j<=m; j++){
				int min = matrix[i-1][j-1] + (arr1[i-1].equals(arr2[j-1])?0:1);
				int temp = matrix[i-1][j] + 1;
				if(temp<min)
					min = temp;
				temp = matrix[i][j-1] + 1;
				if(temp<min)
					min = temp;
				matrix[i][j] = min;				
			}
		}
		return matrix[n][m];
	}
}
