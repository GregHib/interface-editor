import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BruteForce{
    public static void main(String[] args){
        bruteForceHash();
    }

    private static int[] hashes = new int[]{
            22834782
    };

    private static char[] allvalidChars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_', '-'};

    private static void bruteForceHash(){
        int l1hash;
        int l2hash;
        int l3hash;
        int l4hash;
        int l5hash;
        int l6hash;
        int l7hash;
        int l8hash;
        int l9hash;
        int hash;
        for(char c1: allvalidChars){
            l1hash = c1 - 32;
            for(char c2 : allvalidChars){
                l2hash = l1hash * 61 + c2 - 32;
                for(char c3 : allvalidChars){
                    l3hash = l2hash * 61 + c3 - 32;
                    System.out.println("First 3 characters (out of 9): " + c1 + c2 + c3);
                    for(char c4 : allvalidChars){
                        l4hash = l3hash * 61 + c4 - 32;
                        for(char c5 : allvalidChars){
                            l5hash = l4hash * 61 + c5 - 32;
                            for(char c6 : allvalidChars){
                                l6hash = l5hash * 61 + c6 - 32;
                                for(char c7 : allvalidChars){
                                    l7hash = l6hash * 61 + c7 - 32;
                                    for(char c8 : allvalidChars){
                                        l8hash = l7hash * 61 + c8 - 32;
                                        for(char c9 : allvalidChars){
                                            l9hash = l8hash * 61 + c9 - 32;
                                            hash = addToHash(".DAT", l9hash);
                                            if(getMatch(hash) != -1)
                                                appendToBruteForceHashLog(new StringBuilder().append(c1).append(c2).append(c3).append(c4).append(c5).append(c6).append(c7).append(c8).append(c9).append(".DAT").toString(), hash);
                                            hash = addToHash(".IDX", l9hash);
                                            if(getMatch(hash) != -1)
                                                appendToBruteForceHashLog(new StringBuilder().append(c1).append(c2).append(c3).append(c4).append(c5).append(c6).append(c7).append(c8).append(c9).append(".IDX").toString(), hash);
                                            if(getMatch(l9hash) != -1)
                                                appendToBruteForceHashLog(new StringBuilder().append(c1).append(c2).append(c3).append(c4).append(c5).append(c6).append(c7).append(c8).append(c9).toString(), l9hash);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static int addToHash(String s, int hash){
        for(int j = 0; j < s.length(); j++)
            hash = (hash * 61 + s.charAt(j)) - 32;
        return hash;
    }

    private static int getMatch(int hash){
        for(int i = 0; i < hashes.length; i++)
            if(hashes[i] == hash)
                return i;
        return -1;
    }

    private static int getHashForName(String dataName){
        int dataNameHash = 0;
        for(int j = 0; j < dataName.length(); j++)
            dataNameHash = (dataNameHash * 61 + dataName.charAt(j)) - 32;
        return dataNameHash;
    }

    private static void appendToBruteForceHashLog(String name, int hash){
        PrintWriter out = null;
        try{
            out = new PrintWriter(new BufferedWriter(new FileWriter("nameHashCombination.txt", true)));
            out.println("\"" + name.trim() + "\"\thash: " + hash);
        } catch(IOException e){
            e.printStackTrace();
        } finally{
            if(out != null)
                out.close();
        }
    }
}