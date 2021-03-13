package virnet.management.combinedao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import virnet.management.dao.ClassgroupDAO;
import virnet.management.dao.GroupmemberDAO;
import virnet.management.dao.StuClassDAO;
import virnet.management.dao.UserCharacterDAO;
import virnet.management.dao.UserDAO;
import virnet.management.entity.Class;
import virnet.management.entity.Classgroup;
import virnet.management.entity.Groupmember;
import virnet.management.entity.StuClass;
import virnet.management.entity.User;
import virnet.management.entity.UserCharacter;

public class StudentInfoCDAO {
	private StuClassDAO scDAO= new StuClassDAO();
	private ClassInfoCDAO cDAO = new ClassInfoCDAO();
	private GroupInfoCDAO giCDAO = new GroupInfoCDAO();
	private UserDAO uDAO= new UserDAO();
	private UserCharacterDAO ucDAO= new UserCharacterDAO();
	private GroupmemberDAO mDAO= new GroupmemberDAO();
	private ClassgroupDAO gDAO= new ClassgroupDAO();
	
	@SuppressWarnings("unchecked")
	public List<Class> getMyClass(int userid){
		List<Class> clist = new ArrayList<Class>();
		
		List<StuClass> sclist = this.scDAO.getListByProperty("stuClassUserId", userid);
		
		int s = sclist.size();
		for(int i = 0; i < s; i++){
			Class c = this.cDAO.getClass(sclist.get(i).getStuClassClassId());
			
			if(c != null){
				clist.add(c);
			}
		}
		
		return clist;
	}
//谭睿雄2.6
	public Map<String, Object> deleteStud(String userNickname){
		User u = this.uDAO.getUniqueByProperty("userNickname", userNickname);
		Integer stuClassUserId = u.getUserId();	
		System.out.println("userId:"+stuClassUserId);
//删除groupmember表和classgroup表	
		Groupmember gm = (Groupmember)this.mDAO.getUniqueByProperty("classgroupmemberUserId", stuClassUserId);
		System.out.println("Groupmember:"+gm);
		if(gm!=null){
			Integer cgmgId = gm.getClassgroupmemberGroupId();
			System.out.println("classgroupMemberGroupId:"+cgmgId);
			Classgroup cg = (Classgroup)this.gDAO.getUniqueByProperty("classgroupId", cgmgId);
			String classgroupName = cg.getClassgroupName();
			giCDAO.deleteGroup(classgroupName);	
		}
//删除StuClass表		
		System.out.println("deleteStuClass");
		StuClass stuc = (StuClass)this.scDAO.getUniqueByProperty("stuClassUserId", stuClassUserId);
		scDAO.delete(stuc);
//删除UserCharacter表
		System.out.println("deleteUserCharacter");
		UserCharacter uc = (UserCharacter)this.ucDAO.getUniqueByProperty("userCharacterUserId", stuClassUserId);
		ucDAO.delete(uc);
//删除User表
		System.out.println("deleteUser");
		uDAO.delete(u);
		return null;
	}
}
