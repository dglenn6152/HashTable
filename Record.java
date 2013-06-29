//
// RECORD.JAVA
// Record type for string hash table
//
// A record associates a certain string (the key value) with 
// a list of sequence positions at which that string occurs.
//

import java.util.*;

public class Record {
    public String key;
    public ArrayList<Integer> positions;
    public int placement = -1; //used for quick deletion
    public int hash; //used for quick string comparing
    
    
    public Record(String s)
    {
	key = s;
	positions = new ArrayList<Integer>(1);
	hash = toHashKey(s);
    }
    
    
    int toHashKey(String s)
	{
    	if (s.equals(" ")){ //if Record value is empty
    		return -1; //hash it to -1
    	}
    	if (s.equals("deleted")){//if Record value is deleted
    		return -2;//hash it to -2
    	}
		int A = 1952786893;
		int B = 367257;
		int v = B;
		for (int j = 0; j < s.length(); j++)
		{
			char c = s.charAt(j);
			v = A * (v + (int) c + j) + B;
		}

		if (v < 0) v = -v;
		return v;
	}
}
