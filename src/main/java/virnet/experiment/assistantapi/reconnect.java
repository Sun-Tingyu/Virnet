package virnet.experiment.assistantapi;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import net.sf.json.JSONObject;
import virnet.experiment.operationapi.FacilityConfigure;
import virnet.experiment.operationapi.PCExecute;
import virnet.experiment.resourceapi.CabinetResource;
import virnet.experiment.resourceapi.ResourceAllocate;
import virnet.management.combinedao.CabinetTempletDeviceInfoCDAO;
import virnet.management.combinedao.TaskInfoCDAO;
import virnet.management.dao.ExpDAO;
import virnet.management.dao.ExpTaskDAO;
import virnet.management.entity.Exp;
import virnet.management.entity.ExpTask;

public class reconnect {
	
	public reconnect(){
		
	}
	
	public void recover(JSONObject jsonString ,
			WebSocketSession wss,
			ConcurrentHashMap<WebSocketSession, String> userMap,
 			ConcurrentHashMap<String, String> MapEquipmentName,
 			ConcurrentHashMap<String, String> MapEquipmentNum,
 			ConcurrentHashMap<String, String> MapEquipmentPort,
 			ConcurrentHashMap<String, String> MapEquipment,
 			ConcurrentHashMap<String, String> MapTopo,
 			ConcurrentHashMap<String, String> MapGroupProblem,
 			ConcurrentHashMap<String, FacilityConfigure> groupFacilityConfigureMap,
 			ConcurrentHashMap<String, PCExecute> groupPcConfigureMap,
 			ArrayList<WebSocketSession> expUsers,
 			ArrayList<WebSocketSession> monitorUsers,
 			ConcurrentHashMap<String, List<String>>  MapCommandHistory,
 			ConcurrentHashMap<String, String> MapGroupEndTime,
 			ConcurrentHashMap<String, Integer>MapTaskOrder,
 			ConcurrentHashMap<WebSocketSession, String> MapUserName,
 			ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> groupMemberMap,
 			ConcurrentHashMap<String, String> userFacilityConfigureMap,
            ConcurrentHashMap<String, String> userPcConfigureMap,
            ConcurrentHashMap<String, String> MapEquipmentIp
			 			) throws IOException{
		
		JSONObject ss = jsonString;
		
		System.out.println("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");
		System.out.println(jsonString);
		
		
		
		// ????????????
		String expId = jsonString.getString("expId");
		CabinetTempletDeviceInfoCDAO ctdDAO = new CabinetTempletDeviceInfoCDAO();
		
		String groupId = userMap.get(wss);
		
		// ??????????????????
		Integer equipmentNumber = ctdDAO.equipmentNumber(expId);

		// ??????????????????##???????????????????????????????????????????????????????????????(RT##SW2##SW2#SW3#PC##PC)
		String name_Str = ctdDAO.equipment(expId);		
		
		// ????????????
		TaskInfoCDAO tcDAO = new TaskInfoCDAO();
		Integer taskNum = tcDAO.taskNum(expId);
		Integer expTaskOrder = MapTaskOrder.get(groupId);
		System.out.println(expTaskOrder);
		
		ExpTaskDAO etDAO = new ExpTaskDAO();
		ExpTask exptask = new ExpTask();
		ExpDAO expDAO = new ExpDAO();
		Exp exp = new Exp();
		exptask = (ExpTask)etDAO.getByNProperty("expId",expId,"expTaskOrder",expTaskOrder.toString());
		exp = (Exp)expDAO.getUniqueByProperty("expId", expId);
		String taskname = exptask.getExpTaskContent();
		String expname = exp.getExpName();
		
		jsonString.put("type", "requestEquipment");
		jsonString.put("equipmentNumber", "" + equipmentNumber);
		jsonString.put("equipmentName", name_Str);
		jsonString.put("expTaskNum", "" + taskNum);
		jsonString.put("expTaskOrder", "" + expTaskOrder);
		jsonString.put("TaskName", taskname);
		jsonString.put("expName", expname);
		String mess1 = jsonString.toString();
		wss.sendMessage(new TextMessage(mess1));

		// ??????
		String name_str = MapEquipmentName.get(groupId);
		String num_str = MapEquipmentNum.get(groupId);
		String port_str = MapEquipmentPort.get(groupId);
		String cabinet_num = MapEquipment.get(groupId);

		jsonString.put("type", "sendEquipment");
		jsonString.put("equipmentName", name_str);
		jsonString.put("equipmentNumStr", num_str);
		jsonString.put("equipmentPortStr", port_str);
		jsonString.put("cabinet_num", cabinet_num);

		String mess2 = jsonString.toString();
		wss.sendMessage(new TextMessage(mess2));
		
		if(MapTopo.get(groupId) == null ){
			System.out.println("################################");
			String position = "";
			ss.put("position", position);
			ss.put("type", "equipConnectionInfo");
			String mess3 = ss.toString();
			wss.sendMessage(new TextMessage(mess3));
			
		}else{
			// ???????????????????????????????????????
			String[] topoInfo = MapTopo.get(groupId).split("@");
			
			jsonString.put("type", "equipConnectionInfo");
			jsonString.put("position", topoInfo[0]);
			jsonString.put("leftNUM_Str", topoInfo[1]);
			jsonString.put("rightNUM_Str", topoInfo[2]);
			jsonString.put("leftport_Str", topoInfo[3]);
			jsonString.put("rightport_Str", topoInfo[4]);

			String mess3 = jsonString.toString();
			wss.sendMessage(new TextMessage(mess3));
		}

		// ????????????????????????????????????
		for (int i = 0; i < equipmentNumber; i++) {
			if (groupFacilityConfigureMap.containsKey(groupId + i)
					|| groupPcConfigureMap.containsKey(groupId + i)) {
				// ????????????????????????
				System.out.println("?????????????????????");
				for(Integer j=0;j<expUsers.size();j++){
					if(userMap.get(expUsers.get(j)) == groupId && (userFacilityConfigureMap.containsKey(expUsers.get(j)+j.toString()) 
							|| userPcConfigureMap.containsKey(expUsers.get(j)+j.toString()))){
						
						Integer controllerNum = groupMemberMap.get(groupId).get(MapUserName.get(expUsers.get(j)));
						jsonString.put("equipmentControler", controllerNum);
					}
				}
				jsonString.put("type", "lock");
				jsonString.put("lock", "lock");
				jsonString.put("inputEquipmentNumber", i);
				jsonString.put("isMyself", false);
				String mess = jsonString.toString();
				wss.sendMessage(new TextMessage(mess));
			}
		}
//		// ????????????????????????
//		jsonString.put("type", "getOperationHistory");
//
//		// ???????????????????????????
//		long currentTimelong = System.currentTimeMillis();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ss");
//		String currentTime = df.format(currentTimelong);
//
//		// ???????????????websocketSession,??????????????????????????????????????????????????????
//		MapWebsocket.put(currentTime, wss);
//		jsonString.put("websocketSession", currentTime);
//		String mess = jsonString.toString();
//
//		for (WebSocketSession user : expUsers) {
//
//			// ???????????????????????????
//			if (user.isOpen() && (userMap.get(user).equals(groupid)) && (!user.equals(wss))) {
//				user.sendMessage(new TextMessage(mess));
//				break;
//			}
//		}
		String endTime = MapGroupEndTime.get(groupId);
		jsonString.put("type", "timeRemain");
		jsonString.put("content",endTime);
		String mess3 = jsonString.toString();
		wss.sendMessage(new TextMessage(mess3));
		
		String history = "";
		List<String> commandHistory = MapCommandHistory.get(groupId);
		for(int i=0; i<commandHistory.size();i++ ){
			history = history + commandHistory.get(i) + "###";
		}
		jsonString.put("content", history.substring(0, history.length()-3));
		jsonString.put("type", "loadOperationHistory");
		String mess = jsonString.toString();
		wss.sendMessage(new TextMessage(mess));

		try {

			if (jsonString.getString("expRole").equals("stu")|| jsonString.getString("expRole").equals("GM")) {
				// ??????????????????
				jsonString.put("content", jsonString.getString("user") + "????????????????????????");
			} else if (jsonString.getString("expRole").equals("monitor")) {

				// ??????????????????
				jsonString.put("type", "disappearHelp");
				jsonString.put("groupId", groupId);
				if (MapGroupProblem.containsKey(groupId))
					MapGroupProblem.remove(groupId);
				sendToMonitor(wss, jsonString,monitorUsers);

				// ??????????????????
				jsonString.put("content", jsonString.getString("user") + "???????????????????????????");
			}
			jsonString.put("color", "#000");
			jsonString.put("user", "????????????");
			jsonString.put("type", "communication");
			sendToGroup(wss, jsonString,userMap,expUsers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("??????????????????");
		addExpUser(wss,userMap,expUsers,MapUserName,groupMemberMap);	
	}
	
	public void reboot(String workgroup, String expId,ConcurrentHashMap<String, String> MapEquipment, ConcurrentHashMap<String, String> MapEquipmentIp){
		
		System.out.println("call reboot .....................");
		
		System.out.println(workgroup);
		String cabinetIp = MapEquipmentIp.get(workgroup);
		System.out.println("cabinetIp==="+cabinetIp);
				
		
		//cabinetNum?????????????????????????????????????????????????????????????????????-?????????2019/06/07
//		Date enddate = new Date(new Date().getTime()+(long)90 * 90 * 1000);
//		SimpleDateFormat endsdf = new SimpleDateFormat("yyyyMMddHHmmss");

		String groupId = workgroup;
		System.out.println("groupId==="+groupId);
		
		String enddate_str = MapEquipment.get(groupId);
		System.out.println("enddate_str==="+enddate_str);
		

		System.out.println("expId==="+expId);
		
		CabinetTempletDeviceInfoCDAO ctdDAO = new CabinetTempletDeviceInfoCDAO();
		// ??????????????????##???????????????????????????????????????????????????????????????(RT##SW2##SW2#SW3#PC##PC)
		String name_Str = ctdDAO.equipment(expId);
		System.out.println("name_str=="+name_Str);
		ResourceAllocate resourceAllocate = new ResourceAllocate(name_Str,enddate_str , cabinetIp);
		
		resourceAllocate.reallocate();
		System.out.println("end......reboot.......");
	}
	
	public void changeCabinet(String workgroup, String expId, String cabinetIP, ConcurrentHashMap<String, String> MapEquipment, ConcurrentHashMap<String, String> MapEquipmentIp){
		
		System.out.println("call change cabinet .....................");
		
		System.out.println(workgroup);
//		String cabinetIp = MapEquipmentIp.get(workgroup);
		System.out.println("cabinetIp==="+cabinetIP);
				
		String groupId = workgroup;
		System.out.println("groupId==="+groupId);
		
		String enddate_str = MapEquipment.get(groupId);
		System.out.println("enddate_str==="+enddate_str);
		

		System.out.println("expId==="+expId);
		
		CabinetTempletDeviceInfoCDAO ctdDAO = new CabinetTempletDeviceInfoCDAO();
		// ??????????????????##???????????????????????????????????????????????????????????????(RT##SW2##SW2#SW3#PC##PC)
		String name_Str = ctdDAO.equipment(expId);
		System.out.println("name_str=="+name_Str);
		ResourceAllocate resourceAllocate = new ResourceAllocate(name_Str,enddate_str , cabinetIP);
		
		resourceAllocate.reallocate();
		System.out.println("end......change cabinet.......");
	}

	public void sendToGroup(WebSocketSession wss, JSONObject jsonString,
			ConcurrentHashMap<WebSocketSession, String> userMap, ArrayList<WebSocketSession> expUsers) {

		String groupid = userMap.get(wss);
		String mess = jsonString.toString();
		System.out.println("??????" + groupid);

		for (WebSocketSession user : expUsers) {
			try {
				if (user.isOpen() && (userMap.get(user).equals(groupid))) {
					user.sendMessage(new TextMessage(mess));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendToMonitor(WebSocketSession wss, JSONObject jsonString, ArrayList<WebSocketSession> monitorUsers) {

		String mess = jsonString.toString();
		for (WebSocketSession user : monitorUsers) {
			try {
				if (user.isOpen()) {
					user.sendMessage(new TextMessage(mess));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
// lrc
	@SuppressWarnings("rawtypes")
	public void addExpUser(WebSocketSession wss,
							ConcurrentHashMap<WebSocketSession, String> userMap,
							ArrayList<WebSocketSession> expUsers,
							ConcurrentHashMap<WebSocketSession, String> MapUserName,
							ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> groupMemberMap){
		//??????????????????????????????????????????
		System.out.println("addExpUsers....");
		String groupid = userMap.get(wss);
		ConcurrentHashMap<String, Integer> MemberMap = groupMemberMap.get(groupid);
		System.out.println("hhhhhhhhhhhh:"+MemberMap);
		String userName = MapUserName.get(wss);
		if(!MemberMap.containsKey(userName)){
			JSONObject job = new JSONObject();
			job.put("type", "addExpUsers");
			String name = MapUserName.get(wss);
			job.put("content", name);
			for (WebSocketSession user : expUsers){
				try {
					if (user.isOpen() && (userMap.get(user).equals(groupid)) && !user.equals(wss)) {
						user.sendMessage(new TextMessage(job.toString()));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//??????????????????????????????????????????
		JSONObject job2 = new JSONObject();
		System.out.println("getAllExpUsers0");
		System.out.println("wss:"+wss);
		String allUsers = new String();
		ArrayList<String> names = new ArrayList<String>();
		Iterator iterator = (MemberMap.values()).iterator();
		System.out.println("sss"+MemberMap);
		System.out.println("getAllExpUsers01");
		int maxMemberNum = 0;
		System.out.println("values!!!!!:"+MemberMap.values());
		try {
			maxMemberNum = 0;
			while(iterator.hasNext()) {
				int temp = (int)iterator.next();
				if(temp > maxMemberNum)
					maxMemberNum = temp;
				//?????????Object??????????????????????????????????????????????????????object.getClass??????
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("max"+maxMemberNum);
		System.out.println("getAllExpUsers02");
		for(int i=0;i<maxMemberNum;i++) {
			names.add(" ");
		}
		System.out.println("getAllExpUsers03");
		try {
			Iterator iterator2 = (MemberMap.keySet()).iterator();
			while(iterator2.hasNext()) {
				String userName2 = (String)iterator2.next();
				int index = MemberMap.get(userName2);
				names.set(index-1, userName2);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("getAllExpUsers1");
		for(int i=0;i<names.size();i++) {
			allUsers = allUsers + (i+1) +"."+ names.get(i) + ";";
		}
		System.out.println("getAllExpUsers2");
		System.out.println("allUsers:"+allUsers);
		job2.put("type", "getAllUsers");
		job2.put("content",allUsers);
		try {
			if (wss.isOpen() && (userMap.get(wss).equals(groupid))) {
				String username = MapUserName.get(wss);
				int memberNum = MemberMap.get(username);
				System.out.println("Your num is:"+ memberNum);
				job2.put("user", memberNum);
				wss.sendMessage(new TextMessage(job2.toString()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("getAllExpUsers3");
		//sendToGroup(wss,job);
	}
}

