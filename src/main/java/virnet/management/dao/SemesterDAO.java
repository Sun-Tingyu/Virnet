package virnet.management.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import virnet.management.basedao.BaseDAO;
import virnet.management.entity.Semester;
import virnet.management.util.PageUtil;
import virnet.management.util.ViewUtil;

public class SemesterDAO extends BaseDAO{
	private ViewUtil vutil = new ViewUtil();
	 public void add(Semester obj) {
        super.add(obj);
    }

   
    public void delete(Semester obj) {
        super.delete(obj);
    }

	public void deleteById(Serializable id) {
        super.deleteById(Semester.class, id);
    }

   
    public void update(Semester obj) {
    	super.update(obj);
    }

   
    public Object get(Serializable id) {
    	Object o = super.get(Semester.class, id);
		return o;
    }

   
	public Object getByNProperty(String... strs) {
    	Object o = super.getByNProperty(Semester.class, strs);
		return o;
    }

	public Object getUniqueByProperty(String pName, Object pValue) {
    	Object o = super.getUniqueByProperty(Semester.class, pName, pValue);
		return o;
    }

   
    public Object getUniqueByHql(String hql) {
    	Object o = super.getUniqueByHql(hql);
    	return o;
    }

	public Object getUniqueBySql(String sql) {
    		Object o = super.getUniqueBySql(sql, Semester.class);
    	return o;
    }

    // ////////////////////查询单个完毕////////////////

   
    @SuppressWarnings({ "rawtypes" })
	public List getList() {
    	List list = super.getList(Semester.class);
    	return list;
    }

   
    @SuppressWarnings({ "rawtypes" })
	public List getListByProperty(String pName,
            Object pValue) {
    	List list = super.getListByProperty(Semester.class, pName, pValue);
    	return list;	    	
    }

   
    @SuppressWarnings({ "rawtypes" })
	public List getListByProperty(String pName, Object pValue, String condition) {
    	List list = super.getListByProperty(Semester.class, pName, pValue, condition);
    	return list;
    }

   
    @SuppressWarnings({ "rawtypes" })
	public List getListByNProperty(String... strs) {
    	List list = super.getListByNProperty(Semester.class, strs);
    	return list;
    }

   
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public List getListByHql(String hql) {
    	List list = super.getListByHql(hql);
    	return list;
    }

   
    @SuppressWarnings("rawtypes")
	public List getListBySql(String sql) {
    	List list = super.getListBySql(sql, Semester.class);
    	return list;
    }

   
	@SuppressWarnings("rawtypes")
	public void getListByPage(PageUtil pageUtil) {
        super.getListByPage(Semester.class, pageUtil);
    }

	@SuppressWarnings("rawtypes")
	public void getListByPage(String hql, PageUtil pageUtil) {
		super.getListByPage(hql, pageUtil);
    }
	
	public List<List<Map<String, Object>>> semesterModified() throws ParseException  {
        List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
        List<Semester> slist = getListByProperty("semesterId", 1);
        
        System.out.println(slist.size());
        
        if(slist.isEmpty()){
            //error
        	System.out.println("error!");
        }
        else{
        	List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
        	
            Map<String, Object> map11 = new HashMap<String, Object>();
            map11.put("name", "学期开始日期");
            Map<String, Object> map12 = new HashMap<String, Object>();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date  now = new Date();
            now=slist.get(0).getSemesterStartdate();
            String d=sdf.format(now);
            System.out.println(d);
            
            map12.put("name",d);
            
            map12.put("class", "btn btn-link edit");
            map12.put("onclick", "editable(this);");
            map12.put("value", "semesterStartDate");
            
            
            list1.add(map11);
            list1.add(map12);
            
            List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
            
            Map<String, Object> map21 = new HashMap<String, Object>();
            map21.put("name", "学期周数");
            
            Map<String, Object> map22 = new HashMap<String, Object>();
            
            String wnum=""+slist.get(0).getSemesterTotalweek();
            System.out.println(wnum);
            map22.put("name", wnum);
            
            map22.put("class", "btn btn-link edit");
            map22.put("onclick", "editable(this);");
            map22.put("value", "weeksNum");
            
         
            list2.add(map21);
            list2.add(map22);
            
            list.add(list1);
            list.add(list2);
        }
       
        return list;
    }
	
	
    public Map<String, Object> editPage(){

        Map<String, Object> map = new HashMap<String, Object>();

        Map<String, Object> tittle = new HashMap<String, Object>();
        tittle.put("data", "学期修改 <i class='icon-double-angle-right'></i> 设置当前学期");
        
         List<List<Map<String, Object>>> list = new ArrayList<List<Map<String, Object>>>();
        // List<Map<String, Object>> semesterStartDate = this.vutil.createList("学期开始日期", "", "", "", "btn btn-link edit", "editable(this);", "semesterStartDate",0);
        // List<Map<String, Object>> weeksNum = this.vutil.createList("学期总周数", "", "", "", "btn btn-link edit", "editable(this);", "weeksNum",0);
        // list.add(semesterStartDate);
        // list.add(weeksNum); 

        try {
			list = semesterModified();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
        Map<String, Object> button = new HashMap<String, Object>();
        button.put("content", "保存");
        button.put("class", "btn button-new");
        button.put("click", "submitSemester();");
        
        map.put("tittle", tittle);
        map.put("data", list);
        map.put("button", button);  
        return map;
    }
}

