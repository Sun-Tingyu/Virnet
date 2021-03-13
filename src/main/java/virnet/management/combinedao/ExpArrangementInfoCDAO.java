//谭睿雄4.5-新建文件
package virnet.management.combinedao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import virnet.management.dao.CaseDAO;
import virnet.management.dao.ClassDAO;
import virnet.management.dao.ClassTeacherDAO;
import virnet.management.dao.ClassarrangeCaseDAO;
import virnet.management.dao.ClassarrangeDAO;
import virnet.management.dao.CourseDAO;
import virnet.management.dao.OrderDAO;
import virnet.management.dao.OrdermemberDAO;
import virnet.management.dao.PeriodarrangeDAO;
import virnet.management.dao.expArrangeDAO;
import virnet.management.entity.Case;
import virnet.management.entity.Class;
import virnet.management.entity.ClassTeacher;
import virnet.management.entity.Classarrange;
import virnet.management.entity.ClassarrangeCase;
import virnet.management.entity.Course;
import virnet.management.entity.Order;
import virnet.management.entity.Ordermember;
import virnet.management.entity.expArrange;
import virnet.management.util.UserInfoProcessUtil;

public class ExpArrangementInfoCDAO{
	private ClassarrangeDAO caDAO = new ClassarrangeDAO();
	private ClassarrangeCaseDAO cacDAO = new ClassarrangeCaseDAO();
	private expArrangeDAO eaDAO =new expArrangeDAO();
	private OrderDAO oDAO = new OrderDAO();
	private OrdermemberDAO omDAO = new OrdermemberDAO();
	private CaseDAO cDAO = new CaseDAO();//trx6.1
	private ClassTeacherDAO ctDAO = new ClassTeacherDAO();
	private Map<String, Object> delete = new HashMap<String, Object>();
	
	public Map<String, Object> deleteExpARG(String id){
		System.out.println("id:"+id);
		String[] icl = id.split("&&");
		int deletetr = Integer.parseInt(icl[0]);
		String username = icl[1];
		UserInfoProcessUtil usercheck = new UserInfoProcessUtil();
		int userid = usercheck.checkUsername(username);
		System.out.println("deletetr:"+deletetr+" username:"+username);
		String hql = "SELECT model from " + ClassTeacher.class.getName() + " as model where model.classTeacherTeacherId ='" + userid + "'";
		List<ClassTeacher> ctlist = new ArrayList<ClassTeacher>();
		ctlist = this.ctDAO.getListByHql(hql);
		int size = ctlist.size();
		System.out.println("Class list size : " + size);
		
		List<Object> selectlist = new ArrayList<Object>();
		for(int i = 0; i < size; i++){
			Map<String, Object> cmap = new HashMap<String, Object>();
			
			int classid = ctlist.get(i).getClassTeacherClassId();
			cmap.put("id", classid);
			
			ClassDAO cDAO = new ClassDAO();
			List<Class> clist = cDAO.getListByProperty("classId", classid);
			int courseid = clist.get(0).getClassCourseId();
			CourseDAO courseDAO = new CourseDAO();
			List<Course> courselist = courseDAO.getListByProperty("courseId", courseid);
			
			cmap.put("class", courselist.get(0).getCourseName() + " " + clist.get(0).getClassName());
			selectlist.add(cmap);
		}
		
		
		int s = selectlist.size();
		int classid;
		if(s == 0){
			//list is null
			classid = -1;
		}
		else{
			classid = (int) ((Map<String, Object>) selectlist.get(0)).get("id");
		}
		
		String ghql = "select distinct t1.expArrangeId "
				+ " from expArrange as t1 ,Periodarrange as t2 , Classarrange as t3, ClassarrangeCase as t4 "
				+ "where t2.periodarrangeClassId = " + classid
				+ " and t2.periodarrangeId = t3.classarrangePeriodArrangeId "
				+ " and t3.classarrangeExpArrangeId = t1.expArrangeId "
				+ " and t4.classarrangeCaseExpArrangeId = t1.expArrangeId ";
		System.out.println("ghql:"+ghql);
		PeriodarrangeDAO paDAO = new PeriodarrangeDAO();
		@SuppressWarnings("unchecked")
		List<Object> expArgList = paDAO.getListByHql(ghql);
					
		System.out.println("要删除的实验活动为" + expArgList.get(deletetr));
		List<Case> Case = cDAO.getListByProperty("caseExpArrangeId", expArgList.get(deletetr));//谭睿雄6.1
		int cs = Case.size();
		if(cs != 0) {
			System.out.println("has case!");
			delete.put("data",0);
		}
		else {
			//删除classarrange表
			List<Classarrange> cal = caDAO.getListByProperty("classarrangeExpArrangeId", expArgList.get(deletetr));
			int cals = cal.size();
			for(int i = 0; i < cals; i++) {
				Classarrange ca = cal.get(i);
				caDAO.delete(ca);
			}
			//删除classarrange_case表
			List<ClassarrangeCase> cacl = cacDAO.getListByProperty("classarrangeCaseExpArrangeId", expArgList.get(deletetr));
			int cacls = cacl.size();
			for(int i = 0; i < cacls; i++) {
				ClassarrangeCase cac = cacl.get(i);
				cacDAO.delete(cac);
			}
			//删除exparrange表
			List<expArrange> eal = eaDAO.getListByProperty("expArrangeId", expArgList.get(deletetr));
			int eals = eal.size();
			for(int i = 0; i < eals; i++) {
				expArrange ea = eal.get(i);
				eaDAO.delete(ea);
			}
			//删除ordermember表和order表-5.1
			List<Order> ol = oDAO.getListByProperty("orderExpArrangeId", expArgList.get(deletetr));
			int ols = ol.size();
			for(int i = 0; i < ols; i++) {
				Order o = ol.get(i);
				int oid = o.getOrderId();
				List<Ordermember> oml = omDAO.getListByProperty("ordermemberOrderId", oid);
				int omls = oml.size();
				for(int j = 0; j < omls; j++) {
					Ordermember om = oml.get(j);
					omDAO.delete(om);
				}
				oDAO.delete(o);
			}
			System.out.println("delete finished");
		}
		return delete;
	}
}