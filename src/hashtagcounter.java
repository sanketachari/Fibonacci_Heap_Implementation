/*
*  This class can be used for counting the hashtags present in the input file provided
*  by the user. It calls utility functions for storing hashtags and its counts.
*
*  Following operations can be performed:
*  1. Remove Max
*  2. Insert hashtag & its key
*  3. Increase Key
*
*  Main method reads the input file and calls the countHashTags which writes the expected
*  output to output_file.txt
*
*  Author: Sanket Achari, UFID - 71096329, sanketachari@ufl.edu
*  Date:  13 November 2016
*/

import java.io.*;

public class hashtagcounter {

    /*   Method which counts the number of hashtags present in the file
     *   Based on input it can insert hashtags, remove max occurred hashtag
     *   and stop processing based on "STOP" input
     *   Input Argument : All the input data
     *   Returns: nothing
     */
    private void countHashTags(String str){

        String[] lines = str.split("\\n");
        String[] parts;

        // Object of utility class which handles fibonacci heap
        FiboHeapHashTag fh = new FiboHeapHashTag();

        try {

            // Writer for writing into output_file.txt
            BufferedWriter out = new BufferedWriter(new FileWriter("output_file.txt"));
            out.close();
            out = new BufferedWriter(new FileWriter("output_file.txt", true));

            for (String line : lines) {

                parts = line.split("\\s");

                // If hashtag, then perform Parse and insert operations
                if (parts.length > 1)
                   parseHashTag(parts, fh);

                //If "STOP", then stop writing and return
                else if (parts[0].toLowerCase().equals("stop"))
                    break;

                //If query, then perform Parse and RemoveMax
                else if (parts.length == 1){

                    if (isNumeric(parts[0]))
                        out.write(parseQuery(Integer.parseInt(parts[0]), fh));
                    else
                        throw new Exception("Invalid Query");
                }
            }

            out.close();
        }
        catch(Exception e){

            e.printStackTrace();
        }
    }

    /*   Method which extracts hashtag and its count. It calls utility method
     *   for insertion of hashtag and its count into the fibonacci heap
     *   Input Argument : input of string array, object of Utility class
     *   Returns: nothing
     */
    private static void parseHashTag(String[] lines, FiboHeapHashTag fh) throws Exception{

        // Split and extract hashtag and count
        String hashtag = lines[0].split("#")[1];
        int increaseBy = Integer.parseInt(lines[1]);

        // Insert hashtag & its count into Fibonacci Heap
        if (hashtag != null && increaseBy >= 0)
            fh.insert(hashtag, increaseBy);
        else
            throw new Exception("Invalid Hashtag or increase key");
    }

    /*   Method which extracts the number of times hashtag having maximum
     *   count has to be removed.
     *   Input Argument : number, object of Utility class
     *   Returns: String of top hashtags
     */
    private static String parseQuery(int n, FiboHeapHashTag fh){

        String tagDetails;
        String[] parts;
        StringBuilder maxtags = new StringBuilder();
        String[] tags = new String[n];
        int[] noofTag = new int[n];

        for (int i = 0; i < n; i++){

            /*  Call removeMax utility method which removes the hashtag
             *  It returns top hashtag in the form of "hashtag:count"
             */
            tagDetails = fh.removeMax();
            parts = tagDetails.split(":");

            // Append top hashtag to the string
            if ( i != n-1)
                maxtags.append(parts[0] + ",");
            else
                maxtags.append(parts[0] + "\n");

            tags[i] = parts[0];
            noofTag[i] = Integer.parseInt(parts[1]);
        }

        for (int i = 0; i < n; i++){
            fh.insert(tags[i], noofTag[i]);
        }

        return maxtags.toString();
    }

    /*   Method which checks whether given string has numeric characters
     *   Input Argument : string
     *   Returns: true if given string has numeric characters else false
     */
    private static boolean isNumeric(String str) {

        for (char c : str.toCharArray())
        {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    /*   Main method which drives all the operations. It reads the input file
     *   and calls the methods for counting hashtags, removing top hashtags.
     *   Input Argument : String which has input file name.
     *   Return: nothing
     */
    public static void main(String[] args){

        hashtagcounter htc = new hashtagcounter();
        StringBuilder sb = new StringBuilder();

        // Read File
        try{
            BufferedReader br = new BufferedReader(new FileReader(args[0]));

            for (String line; (line = br.readLine()) != null; )
                sb.append(line + "\n");

        }
        catch (Exception e){
            System.out.println("Exception occurred while reading of file");
            e.printStackTrace();
        }

        /*  Call the utility method for counting the hashtags & removing the top hashtags
         *  as per information present in the input file
         */
        htc.countHashTags(sb.toString());
    }
}
