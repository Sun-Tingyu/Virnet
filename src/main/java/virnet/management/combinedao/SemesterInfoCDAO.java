package virnet.management.combinedao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import virnet.management.dao.SemesterDAO;
import virnet.management.entity.Semester;
import virnet.management.util.ViewUtil;

public class SemesterInfoCDAO {
    private SemesterDAO smDAO = new SemesterDAO();
    private ViewUtil vutil = new ViewUtil();

    public Map<String, Object> ChangeSemester(Map<String, Object> map) throws ParseException{
    	Map<String, Object> r = new HashMap<String, Object>();
    	Set<String> key = map.keySet();
//    	System.out.println("map_size="+map.size());
//    	System.out.println("key_size="+key.size());
    	
    	Semester s = (Semester) this.smDAO.getUniqueByProperty("semesterId", 1);

    	if(s==null) 
    		return null;
    	
    	System.out.println("time="+s.getSemesterStartdate());
    	System.out.println("weeks="+s.getSemesterTotalweek());
    	
    	
    	String d=(String) map.get("semesterStartDate");
    	if(d!=null) {
    		System.out.println(d);
    		String regex1 = "[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}";
    		String regex2 = "[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}";
    		Pattern p1 = Pattern.compile(regex1);
    		Pattern p2 = Pattern.compile(regex2);
    		
    		Matcher m1 = p1.matcher(d);
    		Matcher m2 = p2.matcher(d);
    		if(m1.find()) {
    			System.out.println(m1.group());
    			if(d.equals(m1.group())) {
    				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        			Date date =sdf.parse(m1.group());
            		s.setSemesterStartdate(date);
    			}
    			else {
    				System.out.println("input error!");
        			r.put("isSuccess",false);
        			return r;
    			}
    			
    		}
    		else if(m2.find()) {
    			System.out.println(m2.group());
    			if(d.equals(m2.group())) {
    				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date =sdf.parse(m2.group());
            		s.setSemesterStartdate(date);
    			}
    			else {
    				System.out.println("input error!");
        			r.put("isSuccess",false);
        			return r;
    			}
    		}
    		else {
    			System.out.println("input error!");
    			r.put("isSuccess",false);
    			return r;
    		}
    		
    	}
    	
    	String weeks1=(String)map.get("weeksNum");
    	if(weeks1!=null) {
    		System.out.println("11111");
    		String regex = "[0-9]{1,2}";
    		Pattern p = Pattern.compile(regex);
    		Matcher m = p.matcher(weeks1);
    		
    		if(m.find()) {
    			System.out.println(m.group());
    			if(weeks1.equals(m.group())) {
    				int ws=Integer.parseInt(m.group());
            		s.setSemesterTotalweek(ws);
    			}
    			else {
    				System.out.println("input error!");
        			r.put("isSuccess",false);
        			return r;
    			}
    		}
    		else {
    			System.out.println("input error!");
    			r.put("isSuccess",false);
    			return r;
    		}
    	}
    	
    	this.smDAO.update(s);
    	
    	r.put("isSuccess", true);
        r.put("name", "");

     	return r;
    }


}