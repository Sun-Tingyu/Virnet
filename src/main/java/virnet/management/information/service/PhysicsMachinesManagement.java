package virnet.management.information.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import virnet.management.dao.PhysicsMachinesDAO;
import virnet.management.entity.Class;
import virnet.management.entity.PhysicsMachines;
import virnet.management.util.PageUtil;

public class PhysicsMachinesManagement implements InformationQuery{
	private PhysicsMachinesDAO physicsMachinesDAO = new PhysicsMachinesDAO();

	/*
	 * @param
	 * page --- required page in database
	 * @return
	 * map : "data" the query list
	 *       "page" total pages
	 * @see virnet.management.information.service.InformationQuery#query(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> query(String user, int page, String select) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<List<Map<String, String>>> list = new ArrayList<List<Map<String, String>>>();
		List<Map<String, String>> head = new ArrayList<Map<String, String>>();
//		Map<String, String> head_id = new HashMap<String, String>();
//		head_id.put("name", "序号");
//		head_id.put("class", "");
//		head.add(head_id);
		
		Map<String, String> head_name = new HashMap<String, String>();
		head_name.put("name", "物理机柜编号");
		head_name.put("class", "");
		head.add(head_name);
		
		Map<String, String> head_status = new HashMap<String, String>();
		head_status.put("name", "状态");
		head_status.put("class", "");
		head.add(head_status);
		
		Map<String, String> head_jump = new HashMap<String, String>();
		head_jump.put("name", "管理设备");
		head_jump.put("class", "");
		head.add(head_jump);
		
		list.add(head);
		
		PageUtil<PhysicsMachines> pageUtil = new PageUtil<PhysicsMachines>();
		if(page == 0){
			page = 1;
		}
		pageUtil.setPageNo(page);
		int PageSize = pageUtil.getPageSize();
		List<PhysicsMachines> physicsmachineslist = new ArrayList<PhysicsMachines>();
		
		int recordSize = physicsMachinesDAO.getList().size();
		System.out.println("记录数："+recordSize);
		if (recordSize > 0) {
//			this.physicsMachinesDAO.getListByPage(pageUtil);
//			physicsmachineslist = pageUtil.getList();
			List<PhysicsMachines> tempList = physicsMachinesDAO.getList();
			Collections.sort(tempList, new Comparator<PhysicsMachines>(){
				/*
	             * int compare(PhysicsMachines p1, PhysicsMachines p2) 返回一个基本类型的整型，
	             	*返回负数表示：p1 小于p2，
	             	* 返回0 表示：p1和p2相等，
	             	* 返回正数表示：p1大于p2
	             */
				public int compare(PhysicsMachines p1, PhysicsMachines p2) {
	                //按照PhysicsMachines的名称进行升序排列
					String s1=p1.getPhysicsMachinesName();
					String s2=p2.getPhysicsMachinesName();
	                if(s1.compareTo(s2)>0){
	                    return 1;
	                }
	                else if(s1.compareTo(s2)==0){
	                    return 0;
	                }
	                return -1;
	            }
			});
			for(int t=0;(t<PageSize)&&(t<recordSize-(page-1)*PageSize);t++) {
				physicsmachineslist.add(tempList.get((page-1)*PageSize+t));
			}
			
			
		}
		int size = physicsmachineslist.size();
		System.out.println("physicsMachinesD list size : " + size);
		
		
		
		
		
		
		
		for(int i = 0; i < size; i++){
			List<Map<String, String>> physicsmachinesinfo = new ArrayList<Map<String, String>>();
			
//			Map<String, String> map_id = new HashMap<String, String>();
//			map_id.put("name", physicsmachineslist.get(i).getPhysicsMachinesId() + "");
//			map_id.put("class", "");
//			physicsmachinesinfo.add(map_id);
			
			Map<String, String> map_name = new HashMap<String, String>();
			map_name.put("name", physicsmachineslist.get(i).getPhysicsMachinesName()+ "");
			map_name.put("class", "btn btn-link");
			map_name.put("onclick", "showDetail('" + physicsmachineslist.get(i).getPhysicsMachinesName() + "', 'physicsMachines');");//physicsMachines detail
			physicsmachinesinfo.add(map_name);
			
			Map<String, String> map_status = new HashMap<String, String>();
			int sflag = physicsmachineslist.get(i).getStatus();
			if(sflag == 1) {
				map_status.put("name", "未分配");
			}
			else {
				map_status.put("name", "可使用");
			}
			map_status.put("class", "");
			physicsmachinesinfo.add(map_status);
			
			Map<String, String> map_jump = new HashMap<String, String>();
			map_jump.put("name", "<i class='icon-arrow-right'></i>");
			map_jump.put("class", "btn btn-new");
			map_jump.put("onclick", "fetchFacilitiesData('facilities-management','" + user + "','0','','" + physicsmachineslist.get(i).getPhysicsMachinesName() + "',false" + ");");
			physicsmachinesinfo.add(map_jump);
			
			Map<String, String> map_delete = new HashMap<String, String>();
			map_delete.put("name", "删除");
			map_delete.put("class", "btn btn-new hide deleteButton");
			map_delete.put("onclick", "deletePhysicsMachine( '"+ physicsmachineslist.get(i).getPhysicsMachinesName() + "');");
			physicsmachinesinfo.add(map_delete);
			
			
			Map<String, String> map_reoccu = new HashMap<String, String>();
			map_reoccu.put("name", "释放与占用");
			//btn btn-new hide 后面是类名
			map_reoccu.put("class", "btn btn-new");
			map_reoccu.put("onclick", "changePhysicsMachineStatus( '"+ physicsmachineslist.get(i).getPhysicsMachinesName() + "');");
			physicsmachinesinfo.add(map_reoccu);
			
			
			System.out.println("index : " + i + ", exp id : " + physicsmachineslist.get(i).getPhysicsMachinesId() + ", exp type : " + physicsmachineslist.get(i).getPhysicsMachinesName());
			
			list.add(physicsmachinesinfo);
		}
		
		int total = this.physicsMachinesDAO.getList().size();
		int pagesize = pageUtil.getPageSize();
		int pageNO = total / pagesize + 1;
		
//		Map<Object, Object> button_reoccu = new HashMap<Object, Object>();
//		button_reoccu.put("content", " 释放-占用 ");
//		button_reoccu.put("class", "btn button-new");
//		button_reoccu.put("click", "showRe_OccuPhysicsMachinesButton();");
//		button_reoccu.put("id", "showRe_OccuPhysicsMachinesButton");
//		//id 须与virnet.js中相同
		
		Map<Object, Object> button = new HashMap<Object, Object>();
		button.put("content", "+ 新增机柜");
		button.put("class", "btn button-new");
		button.put("click", "addContent('physicsMachines-management');");
		
		Map<Object, Object> button_delete = new HashMap<Object, Object>();
        button_delete.put("content", "- 删除机柜");
        button_delete.put("class", "btn button-new");
        button_delete.put("click", "showDeletePhysicsMachinesButton();");
        button_delete.put("id", "showDeletePhysicsMachinesButton");
		
		
		map.put("data", list);
		map.put("page", pageNO);
		
		//按钮添加到map中
//		map.put("button_reoccu",button_reoccu);
		map.put("button_new", button);
		map.put("button_delete", button_delete);
		return map;
	}

}
