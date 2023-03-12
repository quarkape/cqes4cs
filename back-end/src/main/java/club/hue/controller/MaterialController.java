package club.hue.controller;

import club.hue.mapper.MaterialMapper;
import club.hue.mapper.PushMapper;
import club.hue.utils.CheckXSSUtil;
import club.hue.utils.DateTransition;
import club.hue.utils.ResultVOUtil;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static java.lang.Integer.parseInt;

// 所有与材料管理相关的内容都放在这里面
// 包括学分申请与审核，材料提交与审核等
@RestController
public class MaterialController {

    String basePath = "G:\\Projects\\Materials\\cqes4cs\\files\\";
    // String basePath = "/www/apps/cqes4cs/files/";

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private PushMapper pushMapper;

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    // 学生提交学分申请
    @RequestMapping("/createScore")
    @RequiresRoles("student")
    public ResponseEntity createScore(@RequestParam("image") MultipartFile image, @RequestParam Map<String, String> map, HttpServletRequest req) {
        if (image == null) return ResponseEntity.ok().body(ResultVOUtil.error(1, "请上传图片"));
        String name = map.get("name");
        String levelstr = map.get("levelstr");
        String ruleid = map.get("ruleid");
        String[] imageName = image.getOriginalFilename().split("\\.");
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        String fileName = System.currentTimeMillis() + userid;
        String completeName = fileName + "." + imageName[imageName.length - 1];
        // 服务器上面的学分申请存储地址（也是证明材料电子版的存储地址）
        File tempFile = new File(basePath + "material\\", completeName);
        try {
            image.transferTo(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String id = System.currentTimeMillis() + userid;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        // 设定本年1月份和2月份都属于上一个学年的下学期
        if (month < 2) {
            year = year - 1;
        }
        String yearStr = year + "-" + (year + 1);
        int res = materialMapper.addNewAuthentication(id, name, completeName, yearStr, ruleid, levelstr);
        if (res<=0) return ResponseEntity.ok().body(ResultVOUtil.error(1, "提交申请失败"));
        return ResponseEntity.ok().body(ResultVOUtil.success(id));
    }

    // 管理员端获取学生的学分申请
        @RequestMapping("/getAllAuthentications")
        @RequiresRoles("manager")
    public ResponseEntity getAllAuthentications(String curPage, String pageSize, String state, String year, String major, HttpServletRequest req) {
        Integer curPageInt = parseInt(curPage);
        Integer pageSizeInt = parseInt(pageSize);
        Integer stateInt = parseInt(state);
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String managerId = (String) redisMap.get("userid");
        List<HashMap<String, Object>> lists = materialMapper.getAllAuthentications((curPageInt-1)*pageSizeInt, pageSizeInt, stateInt, year, major, managerId);
        Integer total = materialMapper.getAuthenticationsCount(stateInt, year, major);
        HashMap<String, Object> map = new HashMap<>();
        map.put("items", lists);
        map.put("total", total);
        return ResponseEntity.ok().body(ResultVOUtil.success(map));
    }

    // 学生根据学号获取所有的申请学分记录
    @RequestMapping("/getAuthenticationsById")
    @RequiresRoles("student")
    public ResponseEntity getAuthenticationsById(String curPage, String pageSize, String year, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        Integer curPageInt = parseInt(curPage);
        Integer pageSizeInt = parseInt(pageSize);
        List<HashMap<String, Object>> lists = materialMapper.getAuthenticationsById(userid, (curPageInt-1)*pageSizeInt, pageSizeInt, year);
        Integer total = materialMapper.getAuthenticationByIdCount(userid, year);
        HashMap<String, Object> returnMap = new HashMap<>();
        returnMap.put("items", lists);
        returnMap.put("total", total);
        return ResponseEntity.ok().body(ResultVOUtil.success(returnMap));
    }

    // 学生端通过id获取学分申请的详细内容
    @RequestMapping("/getAuthenticationById")
    @RequiresRoles("student")
    public ResponseEntity getAuthenticationById(String id) {
        HashMap<String, Object> map = materialMapper.getAuthenticationById(id);
        return ResponseEntity.ok().body(ResultVOUtil.success(map));
    }

    // 学生端设置证明材料为异议状态
    @RequestMapping("/submitAgainst")
    @RequiresRoles("student")
    public ResponseEntity submitAgainst(String id, String desc) {
        Integer res = materialMapper.submitAgainst(id, CheckXSSUtil.checkXSS(desc));
        if (res != -1) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(1, "网络出现问题，请联系管理员"));
    }

    // 学生端撤回一项学分申请异议
    @RequestMapping("/recallAgainst")
    @RequiresRoles("student")
    public ResponseEntity recallAgainst(String id, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        // 这里需要验证当前的id是不是用户本人的
        if (!id.substring(13).equals(userid)) {
            return ResponseEntity.ok().body(ResultVOUtil.error(1, "操作失败，没有相应权限"));
        }
        materialMapper.recallAgainst(id);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 教师端获取学生某个学分申请的详细信息
    @RequestMapping("/getCertainAuthenticationById")
    @RequiresRoles("manager")
    public ResponseEntity getCertainAuthenticationById(String id) {
        HashMap<String, Object> res = materialMapper.getCertainAuthenticationById(id);
        // 如果是属于可以自动加分的类型的话，就获取加分配置，如果不属于，则不获取配置，并且由前端将分数设置为0分或者1分（常用的分数即可）
        HashMap<String, Object> resMap = new HashMap<>();
        String config = materialMapper.getClassConfigByType(id);
        resMap.put("item", res);
        resMap.put("config", config);
        return ResponseEntity.ok().body(ResultVOUtil.success(resMap));
    }

    // 教师端获取学生某个学分申请的详细信息
    @RequestMapping("/getCertainAuthenticationByIdWithScore")
    @RequiresRoles("manager")
    public ResponseEntity getCertainAuthenticationByIdWithScore(String id) {
        HashMap<String, Object> res = materialMapper.getCertainAuthenticationById(id);
        Float score = materialMapper.getCertainAuthenticationByIdWithScore(id);
        // 如果是属于可以自动加分的类型的话，就获取加分配置，如果不属于，则不获取配置，并且由前端将分数设置为0分或者1分（常用的分数即可）
        HashMap<String, Object> resMap = new HashMap<>();
        String config = materialMapper.getClassConfigByType(id);
        resMap.put("item", res);
        resMap.put("config", config);
        resMap.put("score", score);
        return ResponseEntity.ok().body(ResultVOUtil.success(resMap));
    }

    // 教师端通过学生的学分申请审核
    @RequestMapping("/checkAuthenticationPass")
    @RequiresRoles("manager")
    public ResponseEntity checkAuthenticationPass(String id, String name, String score, String isModify, String ruleid, String levelstr, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        String curTime = System.currentTimeMillis() + "";
        int state = 1;
        // state=2表示直接审核通过，教师没有修改
        if (isModify.equals("1")) state = 2;
        Integer res = materialMapper.checkAuthenticationPass(name, userid, curTime, state, ruleid, levelstr, id);
        Float scoreFloat = Float.parseFloat(score);
        // 如果需要的话，要对结果保留两位小数
        // Float ss = (float)(Math.round(scoreFloat*100))/100;
        Integer ress = materialMapper.addNewMaterialScore(id, scoreFloat);
        // 在数据返回之前还要查询这一个材料所属的类型以及对应类型的加分是多少
        if (res != -1 && ress != -1) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(1, "操作失败，请联系开发者"));
    }

    // 教师端通过学生的学分申请审核
    @RequestMapping("/checkAuthenticationPassModify")
    @RequiresRoles("manager")
    public ResponseEntity checkAuthenticationPassModify(String id, String name, String score, String isModify, String ruleid, String levelstr, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        String curTime = System.currentTimeMillis() + "";
        Integer res = 0;
        // 没有修改，审核通过
        if (isModify.equals("3")) {
            res = materialMapper.checkAuthenticationPassModify(name, userid, curTime, ruleid, levelstr, id);
        } else {
            // 标记为修改通过
            res = materialMapper.checkAuthenticationPass(name, userid, curTime, 2, ruleid, levelstr, id);
        }
        Float scoreFloat = Float.parseFloat(score);
        // 如果需要的话，要对结果保留两位小数
        // Float ss = (float)(Math.round(scoreFloat*100))/100;
        Integer ress = materialMapper.updateMaterialScore(id, scoreFloat);
        // 在数据返回之前还要查询这一个材料所属的类型以及对应类型的加分是多少
        if (res != -1 && ress != -1) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(1, "操作失败，请联系开发者"));
    }

    // 教师端审核不通过某一项学分申请/删除一项学分申请
    @RequestMapping("/checkAuthenticationNotPass")
    @RequiresRoles("manager")
    public ResponseEntity checkAuthenticationNotPass(String id, String content) {
        String imgUrl = materialMapper.getAuthenticationImgUrlById(id);
        //定义文件路径
        File file = new File(basePath + "material\\" + imgUrl);
        try {
            boolean flag = file.delete(); // 删除照片
            if (flag) {
                // 分别删除学分申请和对应的分值两张表的数据
                materialMapper.checkAuthenticationNotPassA(id);
                materialMapper.checkAuthenticationNotPassB(id);
                String uid = System.currentTimeMillis() + id.substring(13);
                String title = "学分申请审核不通过通知";
                String postTime = DateTransition.DateMills2String(Long.parseLong(id.substring(0,13)));
                String detail = "同学你好，你于【" + postTime + "】提交的学分申请审核不通过或者被删除，原因是：【" + content + "】。如有疑问，请联系辅导员。";
                materialMapper.addNotifyWhenCheckNotPassed(uid, title, CheckXSSUtil.checkXSS(detail));
                return ResponseEntity.ok().body(ResultVOUtil.successSimple());
            } else {
                // 删除失败
                return ResponseEntity.ok().body(ResultVOUtil.error(2, "操作失败，请联系开发人员"));
            }
        } catch (Exception e) {
            // 文件不存在等原因
            e.printStackTrace();
            return ResponseEntity.ok().body(ResultVOUtil.error(3, "操作失败，请联系开发人员"));
        }
    }

    // 学生自己撤销一条尚未审核的学分申请
    @RequestMapping("/checkAuthenticationNotPassStudent")
    @RequiresRoles("student")
    public ResponseEntity checkAuthenticationNotPassStudent(String id, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        // 这里需要验证当前的id是不是用户本人的
        if (!id.substring(13).equals(userid)) {
            return ResponseEntity.ok().body(ResultVOUtil.error(4, "操作失败，没有相应权限"));
        }
        // 这里需要验证当前的学分申请是待审核状态
        int state = materialMapper.checkAuthenticationState(id);
        if (state != 0) {
            return ResponseEntity.ok().body(ResultVOUtil.error(5, "操作失败，没有相应权限"));
        }
        String imgUrl = materialMapper.getAuthenticationImgUrlById(id);
        //定义文件路径
        File file = new File(basePath + "material\\" + imgUrl);
        try {
            boolean flag = file.delete(); // 删除照片
            if (flag) {
                // 分别删除学分申请和对应的分值两张表的数据
                materialMapper.checkAuthenticationNotPassA(id);
                return ResponseEntity.ok().body(ResultVOUtil.successSimple());
            } else {
                // 删除失败
                return ResponseEntity.ok().body(ResultVOUtil.error(2, "操作失败，请联系开发人员"));
            }
        } catch (Exception e) {
            // 文件不存在等原因
            e.printStackTrace();
            return ResponseEntity.ok().body(ResultVOUtil.error(3, "操作失败，请联系开发人员"));
        }
    }

    // 导出评价结果文件
    @RequestMapping("/getEvaluateResult")
    @RequiresRoles("manager")
    public ResponseEntity getEvaluateResult(String year, String major, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String managerId = (String) redisMap.get("userid");
        String fileName = UUID.randomUUID() + ".xls";
        materialMapper.exportResultResultFile(year, major, managerId, basePath + "exports\\" + fileName);
        return ResponseEntity.ok().body(ResultVOUtil.success(fileName));
    }

    // 教师获取异议学分申请列表
    @RequestMapping("/getAgainstList")
    @RequiresRoles("manager")
    public ResponseEntity getAgainstList(String curPage, String pageSize, String action, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String managerId = (String) redisMap.get("userid");
        List<HashMap<String, Object>> list = materialMapper.getAgainstList(parseInt(curPage)-1, parseInt(pageSize), managerId, parseInt(action));
        int total = materialMapper.getAgainstListTotal(managerId, parseInt(action));
        HashMap<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("list", list);
        return ResponseEntity.ok().body(ResultVOUtil.success(map));
    }

    // 教师驳回学分申请异议
    @RequestMapping("/modifyAgainstDismiss")
    @RequiresRoles("manager")
    public ResponseEntity modifyAgainst(String id, String descb, String name, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String managerId = (String) redisMap.get("userid");
        // 修改数据
        materialMapper.modifyAgainst(id, CheckXSSUtil.checkXSS(descb), 3);
        // 给学生发送通知
        String userid = id.substring(13);
        String timeStamp = System.currentTimeMillis() + userid;
        pushMapper.addOneNotify(timeStamp, "学分申请异议申请驳回通知", "同学您好，您提交的关于【" + CheckXSSUtil.checkXSS(name) + "】的异议被驳回，理由是【"+ CheckXSSUtil.checkXSS(descb) +"】。如果您还需要继续提出异议，请直接联系老师。");
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 教师通过学分申请异议
    @RequestMapping("/modifyAgainstPass")
    @RequiresRoles("manager")
    public ResponseEntity modifyAgainstPass(String id, String name, String score, String ruleid, String levelstr, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String managerId = (String) redisMap.get("userid");
        String curTime = System.currentTimeMillis() + "";
        Integer res = 0;
        // 标记为修改通过
        res = materialMapper.checkAuthenticationPass(CheckXSSUtil.checkXSS(name), managerId, curTime, 2, ruleid, levelstr, id);
        Float scoreFloat = Float.parseFloat(score);
        // 如果需要的话，要对结果保留两位小数
        materialMapper.updateMaterialScore(id, scoreFloat);
        materialMapper.modifyAgainst(id, "", 2);
        // 给学生发送通知
        String userid = id.substring(13);
        String timeStamp = System.currentTimeMillis() + userid;
        pushMapper.addOneNotify(timeStamp, "学分申请异议申请通过通知", "同学您好，您提交的关于【" + CheckXSSUtil.checkXSS(name) + "】的异议已通过，请及时检查修改结果。");
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }
}

