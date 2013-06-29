//
// STRINGTABLE.JAVA
// A hash table mapping Strings to their positions in the the pattern sequence
// You get to fill in the methods for this part.
//

public class StringTable {
	Record[] Table = new Record[2];
	int length = 2;
	int filled = 0;
	double minLoadFactor = .3;
	double loadFactor = 0;
	int powerOfListSize = 1;

	//
	// Create an empty table big enough to hold maxSize records.
	//
	public StringTable(int maxSize) 
	{
		for (int j = 0; j < length; j++){ //fills table with empty slots
			Table[j] = new Record(" ");
		}
	}


	//
	// Insert a Record r into the table.  Return true if
	// successful, false if the table is full.  You shouldn't ever
	// get two insertions with the same key value, but you may
	// simply return false if this happens.
	//
	public boolean insert(Record r) 
	{
		fill(); //Adjust the load factor
		int hash = toHashKey(r.key); //Find r's hash value
		int baseValue = baseHash(hash,length)%length; //Initial slot # to try
		if (Table[baseValue].hash == -1 || Table[baseValue].hash == -2){// Is slot empty or full respectively?
			r.placement = baseValue; //set records value to know its placement in slot
			Table[baseValue] = r; // put record into slot
			return true;
		}
		else {
			int step = stepHash(hash,length); //finds incrementor value
			int current = (baseValue + step)% length; //checks first slot after base
			while (current != baseValue){ //cycle through table until reaching first value again
				if (Table[current].hash == -1 || Table[current].hash == -2){ // Is slot empty or full respectively?
					r.placement = current; //set records value to know its placement in slot
					Table[current] = r;// put record into slot
					return true;
				}
				else {
					current = (current + step) % length; //Cycle through table without going over the newlength
				}
			}
			return false; 
		}
	}
	//
	// Used to rebuild the list after full
	//
	public Record[] reinsert(Record[] temp, Record r, int newlength) 
	{
		int hash = toHashKey(r.key); //Find r's hash value
		int baseValue = (baseHash(hash,newlength))% newlength; //Initial slot # to try
		if (Table[baseValue].hash == -1 || Table[baseValue].hash == -2){ // Is slot empty or full respectively?
			r.placement = baseValue; //set records value to know its placement in slot
			temp[baseValue] = r; // put record into slot
			return temp; 
		}
		else {
			int step = stepHash(toHashKey(r.key),newlength); //finds incrementor value
			int current = (baseValue + step)% newlength; //checks first slot after base
			while (current != baseValue){ //cycle through table until reaching first value again
				if (Table[current].hash == -1 || Table[current].hash == -2){ // Is slot empty or full respectively?
					r.placement = current; //set records value to know its placement in slot
					temp[current] = r;// put record into slot
					return temp;
				}
				else {
					current = (current + step) % newlength; //Cycle through table without going over the newlength
				}
			}
			return temp; 
		}

	}
	//
	// Find a record with a key matching the input.  Return the
	// record if it exists, or null if no matching record is found.
	//
	public Record find(String key) {
		int hash = toHashKey(key);
		int baseValue = (baseHash(hash,length))% length;
		if (Table[baseValue].hash == -1){ //If slot is empty
			return null;
		}
		else if (Table[baseValue].key.equals(key)){ //if the slot is the key we are looking for
			return Table[baseValue];
		}
		else {
			int step = (stepHash(hash,length)); //checks first slot after base
			int current = (baseValue + step)% length; //checks first slot after base
			while (current != baseValue){ //cycle through table until reaching first value again
				if (Table[current].hash == -1){//If slot is empty
					return null;
				}
				else if (Table[current].key.equals(key)){ //if the slot is the key we are looking for
					return Table[current];
				}
				else {
					current = (current + step) % length; //Cycle through table without going over the newlength
				}
			}
		}
		return null;
	}


	//
	// Delete a Record r from the table.  Note that you'll have to
	// find the record first unless you keep some extra information
	// in the Record structure.
	//
	public void remove(Record r) 
	{
		filled--; //reduce load factor
		if (r.placement != -1){ //if r's expected position isnt null
			Record newRecord = new Record("deleted"); //make a new record with value "deleted
			newRecord.placement = -2; //set position to -1 (as is standard for deleted
			Table[r.placement] = newRecord; //puts deleted slot in table
		}	
	}
	//
	// Increments the size of the table and doubles it if it exceeds the minimum load factor
	//
	public void fill() 
	{
		filled++; //increment load factor
		loadFactor = ((double)filled / (double)length);
		if (loadFactor > minLoadFactor) { //if load factor surpass a predetermined threshold
			doubleTable(); //double the table
		}	
	}

	//
	// Creates a new table double the length, and fills it with all value from out current table
	// Then replaces the old table with the new table
	//
	public void doubleTable() 
	{
		powerOfListSize++;
		int newlength = (int)(Math.pow(2,powerOfListSize));  //Creates empty table twice the size of the current table
		int oldlength = length; //preserves old lengths value
		Record[] TempTable = Table; //preserve table value
		Table = new Record[newlength]; //makes original table twice the size
		for (int j = 0; j < newlength; j++){
			Table[j] = new Record(" "); //fill table with empty slots
		}
		length = newlength; //sets length to the new length
		for (int j = 0; j < oldlength; j++){
			if (TempTable[j].hash >0){
				Table = reinsert(Table, TempTable[j],newlength); //refills new table with all value from old table so they get hashed properly
			}
		}
	}


	///////////////////////////////////////////////////////////////////////


	// Convert a String key into an integer that serves as input to hash
	// functions.  This mapping is based on the idea of a linear-congruential
	// pesudorandom number generator, in which successive values r_i are 
	// generated by computing
	//    r_i = ( A * r_(i-1) + B ) mod M
	// A is a large prime number, while B is a small increment thrown in
	// so that we don't just compute successive powers of A mod M.
	//
	// We modify the above generator by perturbing each r_i, adding in
	// the ith character of the string and its offset, to alter the
	// pseudorandom sequence.
	//
	int toHashKey(String s)
	{
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
	// 
	//Finds h1 base slots
	//
	int baseHash(int hashKey,int length)
	{
		double constant = (Math.sqrt(3) - 1);
		int base =  (int) Math.floor((length * ((constant* hashKey) - Math.floor(constant* hashKey)))); 
		return base; //mod value is not applied at the base hashLevel. It is in the insert/find/reinset respectively
	}

	// 
	//Finds h2 base slots
	//
	int stepHash(int hashKey,int length)
	{
		double constant = (Math.sqrt(2)-1)/2;
		int step = (int) Math.floor((length * ((constant* hashKey) - Math.floor(constant* hashKey)))); 
		if (step == 0){//if step value = 0 (frequent at low values of length, step =1
			return 1; //while this could be problematic, at small values of n, this is trivial
		}
		if (step % 2 == 0){  //if step is even
			step++; //add 1 to make it odd
		}
		return step;//mod value is not applied at the base hashLevel. It is in the insert/find/reinset respectively
	}
}
