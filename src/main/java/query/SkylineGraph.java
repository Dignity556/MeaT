package query;

import java.util.*;
import java.util.stream.IntStream;

public class SkylineGraph {

    /*
    (X,B):X行的交集为B，同时B列的交集为X
    计算方法：从0列选到B行，假设选了k列，将k列的元素merge成一个新的数组，数组中元素值为k的就是X，k列就是B.（转置就是行的计算方法）
     */
    public HashMap<String,ArrayList<Integer>> concept_lattice(double[][] matrix,HashMap<String,ArrayList<String>> lattices){
//        //首先将每列元素求和，优化按行组合的复杂度
//        int[] sum_column=column_sum(matrix);
        //先从1到n组合
        int row_num=matrix.length;
        int[] combination_matrix=new int[row_num];

        for(int pre=0;pre<row_num;pre++)
        {
            combination_matrix[pre]=pre+1;
        }
        //之后按行遍历，求k行中值为k的元素
        //首先生成所有排序的组合
        for (int com=1;com<row_num+1;com++)
        {
            ArrayList<ArrayList<Integer>> pre_com=getCombinations(combination_matrix,com);
            ArrayList<String> combinations=new ArrayList<>();
            //遍历当前所有组合，转化为string
            for (int i=0;i<pre_com.size();i++)
            {
                String s="";
                for (int j=0; j<pre_com.get(i).size();j++){
                    if (j==0)
                    {
                        int count=Integer.valueOf(pre_com.get(i).get(j));
                        s+=count;
                    }else{
                        int count=Integer.valueOf(pre_com.get(i).get(j));
                        s+=",";
                        s+=count;
                    }
                }
                combinations.add(s);
            }
            //将string拆成对应的数组
            for (String s:combinations)
            {
                String[] sub_matrix_split=s.split(",");
                double[][] sub_matrix=new double[sub_matrix_split.length][matrix[0].length];
                for (int num=0; num<sub_matrix_split.length;num++)
                {
                    //matrix切片
                    int pre=Integer.valueOf(sub_matrix_split[num])-1;
                    System.arraycopy(matrix[pre],0,sub_matrix[num],0,matrix[0].length);
                }
                cal_xb(sub_matrix,s,lattices);
            }
        }
        //最后merge
        //新建hashmap
        HashMap<String,ArrayList<Integer>> final_lattice=new HashMap<>();
        for(String key:lattices.keySet()){
            if (!key.equals(""))
            {
                ArrayList<Integer> sub_merges=new ArrayList<>();
                for (int pre=0; pre<lattices.get(key).size(); pre++){
                    String[] ints=lattices.get(key).get(pre).split(",");
                    for (String current:ints)
                    {
                        int count=Integer.valueOf(current);
                        if (!sub_merges.contains(count))
                        {
                            sub_merges.add(count);
                        }
                    }
                }
                Collections.sort(sub_merges);
//                String final_merge="";
//                for (int i=0;i<sub_merges.size();i++){
//                    if (i==0)
//                    {
//                        final_merge+=sub_merges.get(i);
//                    }else
//                    {
//                        final_merge+=",";
//                        final_merge+=sub_merges.get(i);
//                    }
//                }
//                final_lattice.put(key,final_merge);
                final_lattice.put(key,sub_merges);
            }
        }
        return final_lattice;
//        for (String key: final_lattice.keySet())
//        {
//            System.out.println("key: "+key+" values: "+final_lattice.get(key));
//        }
    }

    public ArrayList<HashSet<Integer>> skyline_layer(HashMap<String,ArrayList<Integer>> lattices)
    {
        ArrayList<ArrayList<Integer>> values=new ArrayList<>();
        for (String key:lattices.keySet())
        {
            values.add(lattices.get(key));
        }
        boolean flag=true;
        ArrayList<HashSet<Integer>> layers=new ArrayList<>();
        while (flag)
        {
            for (int i=0;i< values.size();i++)
            {
                if (values.get(i).size()>0)
                {
                    flag=true;
                    break;
                }
                flag=false;
            }
            HashSet<Integer> pre_layer=new HashSet();
            int minimum=1000000;
            for (ArrayList<Integer> sets:values)
            {
                //如果比最小的还小，先清空，再把索引加进去
                if (sets.size()<minimum && sets.size()>0)
                {
                    minimum=sets.size();
                    pre_layer.clear();
                    pre_layer.addAll(sets);
                }
                else if (sets.size()==minimum)
                {
                    pre_layer.addAll(sets);
                }
            }
            for (ArrayList<Integer> sub_sets:values)
            {
                for (Integer integer:pre_layer){
                    if (sub_sets.contains(integer))
                    {
                        sub_sets.remove(integer);
                    }
                }
            }
            if (pre_layer.size()>0) {
                layers.add(pre_layer);
            }
        }
        return layers;
    }

    //N个数中取M个数的所有组合，使用递归实现
    //numbers:行数，k:取几个
    public ArrayList<ArrayList<Integer>> getCombinations(int[] numbers, int k) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        generateCombinations(result, new ArrayList<>(), numbers, k, 0);
        return result;
    }

    public static void generateCombinations(ArrayList<ArrayList<Integer>> result, ArrayList<Integer> current, int[] numbers, int k, int start) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < numbers.length; i++) {
            current.add(numbers[i]);
            generateCombinations(result, current, numbers, k, i + 1);
            current.remove(current.size() - 1);
        }
    }

    //计算过程
    //参数：矩阵 matrix，哪几行的组合combination，所有的lattice
//    public void cal_xb(double[][] matrix, String combination, HashMap<String,ArrayList<String>> lattices)
//    {
//        int row_count=matrix.length;
//        int[] merge=new int[matrix[0].length];
//        //合并多行数组为一行数组
//        for (int row=0; row<matrix.length;row++)
//        {
//            for (int element=0; element<matrix[0].length; element++)
//            {
//                merge[element]+=matrix[row][element];
//            }
//        }
//        String select="";
//        //找到列表中值是count的，组合成string
//        for (int i=0; i<merge.length;i++)
//        {
//            if (merge[i]==row_count){
//                if (select.equals(""))
//                {
//                    select+=String.valueOf(i+1);
//                }else{
//                    select+=",";
//                    select+=String.valueOf(i+1);
//                }
//            }
//        }
//        if (lattices.containsKey(select))
//        {
//            lattices.get(select).add(combination);
//        }else{
//            ArrayList<String> values=new ArrayList<>();
//            values.add(combination);
//            lattices.put(select,values);
//        }
////        System.out.println(select);
//    }

    public void cal_xb(double[][] matrix, String combination, HashMap<String, ArrayList<String>> lattices) {
        int rowCount = matrix.length;
        int colCount = matrix[0].length;
        int[] merge = new int[colCount];

        // 合并多行数组为一行数组
        for (int row = 0; row < rowCount; row++) {
            for (int element = 0; element < colCount; element++) {
                merge[element] += matrix[row][element];
            }
        }

        StringBuilder selectBuilder = new StringBuilder();
        // 找到列表中值是rowCount的列索引，并组合成字符串
        for (int i = 0; i < merge.length; i++) {
            if (merge[i] == rowCount) {
                if (selectBuilder.length() > 0) {
                    selectBuilder.append(",");
                }
                selectBuilder.append(i + 1);
            }
        }

        String select = selectBuilder.toString();

        // 更新或创建lattices条目
        lattices.computeIfAbsent(select, k -> new ArrayList<>()).add(combination);
    }


    public static void main(String[] args)
    {
//        Main main=new Main();
//        int[][] matrix = {
//                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
//                {1, 1, 0, 0, 0, 0, 0, 0, 0, 0},
//                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
//                {0, 0, 0, 1, 1, 0, 0, 0, 0, 0},
//                {0, 0, 1, 1, 1, 1, 0, 0, 0, 0},
//                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
//                {0, 0, 0, 1, 1, 0, 1, 1, 0, 0},
//                {0, 0, 0, 1, 0, 0, 1, 0, 1, 0},
//                {0, 0, 0, 1, 1, 0, 1, 1, 1, 1}
//        };
////        int[] count=main.column_sum(matrix);
////        for (int i=0;i<count.length;i++)
////        {
////            System.out.println(count[i]);
////        }
////        int[][] submatrix={
////                {0, 0, 0, 1, 1, 0, 1, 1, 0, 0},
////                {0, 0, 0, 1, 0, 0, 1, 0, 1, 0},
////                {0, 0, 0, 1, 1, 0, 1, 1, 1, 1}
////        };
////        HashMap<String,ArrayList<String>> lattices=new HashMap<>();
////        main.cal_xb(submatrix,"8,9,10",lattices);
////        ArrayList combinations=main.getCombinations(new int[]{1,2,3,4,5,6,7,8,9,10},3);
////        for(int i=0;i<combinations.size();i++)
////        {
////            System.out.println(combinations.get(i));
////        }
//        HashMap<String,ArrayList<String>> lattices=new HashMap<>();
//        HashMap<String,ArrayList<Integer>> final_lattice=main.concept_lattice(matrix,lattices);
////        for (String s: lattices.keySet()){
////            System.out.println("key: "+s+" values: "+lattices.get(s));
////        }
//        ArrayList<ArrayList<Integer>> skyline_layers=main.skyline_layer(final_lattice);
//        for (int i=0;i<skyline_layers.size();i++)
//        {
//            System.out.println("Layer "+(i+1)+": "+skyline_layers.get(i));
//        }
    }

}

