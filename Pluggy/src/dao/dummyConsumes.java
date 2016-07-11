package dao;

import java.util.Random;

public class dummyConsumes {

	private static dummyConsumes instance;

	public static dummyConsumes getInstance(){
		if(instance == null){
			instance = new dummyConsumes();
		}
		return instance;
	}
	
	public long getConsumes(){
		Random r = new Random();
		return r.nextLong();
	}
	
	
}
