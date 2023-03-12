package club.hue.controller;

import club.hue.mapper.BasicInfoMapper;
import club.hue.utils.*;
import com.alibaba.fastjson.JSON;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

@RestController
public class BasicInfoController {

    @Autowired
    private BasicInfoMapper basicInfoMapper;

    @Resource
    private RedisTemplate<String, Serializable> redisTemplate;

    String basePath = "G:\\Projects\\Materials\\cqes4cs\\files\\";
    // String basePath = "/www/apps/cqes4cs/files/";
    @RequestMapping("/getStudentAllScoreMaterials")
    @RequiresRoles("student")
    public ResponseEntity getStudentAllScoreMaterials(String id, String year) {
        List<HashMap<String, Object>> res = basicInfoMapper.getStudentAllScoreMaterials(id, year);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 学生端填写学分申请时根据关键字查询赛事列表
    @RequestMapping("/getContestSearchResultByWords")
    @RequiresRoles("student")
    public ResponseEntity getContestSearchResultByWords(String words) {
        List<HashMap<String, Object>> res = basicInfoMapper.getContestSearchResultByWords(words);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 教师端 学分评价页面获取所有学生的所有成绩
    @RequestMapping("/getAllStudentScore")
    @RequiresRoles(value={"manager","student"},logical = Logical.OR)
    public ResponseEntity getAllStudentScore(String year, String major, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String managerId = (String) redisMap.get("userid");
        List<HashMap<String, Object>> res = basicInfoMapper.getAllStudentScore(year, major, managerId);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 学生获取自己年级的所有综合评价成绩，根据自己的id获取届数和专业
    @RequestMapping("/getAllStudentScoreThemselves")
    @RequiresRoles("student")
    public ResponseEntity getAllStudentScoreThemselves(String year, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String studentId = (String) redisMap.get("userid");
        List<HashMap<String, Object>> res = basicInfoMapper.getAllStudentScoreThemselves(year, studentId);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 教师端获取某个学生的所有学分申请
    @RequestMapping("/getStudentAuthenticationsById")
    @RequiresRoles("manager")
    public ResponseEntity getStudentAuthenticationsById(String userid) {
        List<HashMap<String, Object>> res = basicInfoMapper.getStudentAuthenticationsById(userid);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    //教师端获取具体的赛事的加分规则
    @RequestMapping("/getContestConfig")
    @RequiresRoles(value = {"manager", "student"}, logical = Logical.OR)
    public ResponseEntity getContestConfig() {
        List<HashMap<String, Object>> res = basicInfoMapper.getContestConfig();
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    //教师更新设置赛事规则配置
    @RequestMapping("/modifyContestConfig")
    @Transactional
    @RequiresRoles("manager")
    public ResponseEntity modifyContestConfig(@RequestParam Map<String, String> map) {
        List<HashMap<String, Object>> list = new ArrayList<>();
        Map<String, String> nameMap = new HashMap<>();
        Map<String, String> indexMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("name")) {
                nameMap = JSON.parseObject(entry.getValue(), HashMap.class);
            }
            if (entry.getKey().equals("index")) {
                indexMap = JSON.parseObject(entry.getValue(), HashMap.class);
            }
        }

        // 清空配置表
        if (nameMap.size() == 0 || indexMap.size() == 0) {
            basicInfoMapper.deleteContestConfig();
            return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        }
        // 组装list
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("name")) break;
            if (entry.getKey().equals("index")) break;
            HashMap<String, Object> mapp = new HashMap<>();
            mapp.put("uuid", entry.getKey());
            mapp.put("config", entry.getValue());
            mapp.put("name", nameMap.get(entry.getKey()));
            mapp.put("indexid", indexMap.get(entry.getKey()));
            list.add(mapp);
        }
        Boolean flag = false;
        try {
            // 先存入临时表
            basicInfoMapper.updateContestConfigTemp(list);
            // 再删除原始表里面的所有数据
            basicInfoMapper.deleteAllContestConfig();
            // 最后将临时表的数据复制到原始表中
            basicInfoMapper.updateContestConfig();
            // 最后删除临时的规则表，方便下一次的数据存储
            basicInfoMapper.deleteContestsConfigTemp();
        } catch (Exception e) {
            flag = true;
            throw new RuntimeException("New Error");
        }
        if (flag) return ResponseEntity.ok().body(ResultVOUtil.error(1, "操作失败，请联系管理员"));
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 学生获取自己的学业成绩和综合成绩
    @RequestMapping("/getFinalGradeById")
    @RequiresRoles("student")
    public ResponseEntity getFinalGradeById(String year, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> resMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) resMap.get("userid");
        HashMap<String, Object> res = basicInfoMapper.getFinalGradeById(userid, year);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 获取所有的班级列表
    @RequestMapping("/getAllClasses")
    @RequiresRoles("manager")
    public ResponseEntity getAllClasses() {
        List<HashMap<String, Object>> res = basicInfoMapper.getAllClasses();
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 管理员获取教师列表
    @RequestMapping("/getManagerList")
    @RequiresRoles("system")
    public ResponseEntity getManagerList() {
        List<HashMap<String, Object>> res = basicInfoMapper.getManagerList();
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 教师和学生可以获取某个学生的手动加分和减分项
    @RequestMapping("/getScoreManually")
    @RequiresRoles(value = {"manager", "student"}, logical = Logical.OR)
    public ResponseEntity getScoreManually(String userid, String year) {
        List<HashMap<String, Object>> res = basicInfoMapper.getScoreManually(userid, year);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 教师删除某个手动加分项
    @RequestMapping("/deleteScoreManually")
    @RequiresRoles("manager")
    public ResponseEntity deleteScoreManually(String uid) {
        int res = basicInfoMapper.deleteScoreManually(uid);
        if (res == -1) return ResponseEntity.ok().body(ResultVOUtil.error(1, "删除失败！"));
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 教师修改某一项手动加分项的信息，不涉及增删
    @RequestMapping("/modifyScoreManuallyInfo")
    @RequiresRoles("manager")
    public ResponseEntity modifyScoreManuallyInfo(String uid, String score, String type, String desc) {
        int res = basicInfoMapper.modifyScoreManually(Double.parseDouble(score), Integer.parseInt(type), CheckXSSUtil.checkXSS(desc), uid);
        if (res == -1) return ResponseEntity.ok().body(ResultVOUtil.error(1, "修改失败！"));
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 系统接收教师上传的成绩excel并存入数据库
    @RequestMapping("/readXLSFiles")
    @RequiresRoles("manager")
    public ResponseEntity readXLSFiles(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        // 这里注意一定要用\\.来分割才行，因为在java的正则表达式中，点号表示任意字符
        String[] fileNameParts = fileName.split("\\.");
        String fileFinalName = System.currentTimeMillis() + "." + fileNameParts[fileNameParts.length - 1];
        String filePath = basePath + "grades\\";
        File tempFile = new File(filePath, fileFinalName);
        boolean isSucceed = false;
        try {
            file.transferTo(tempFile);
            List<HashMap<String, Object>> list = ReadGradeXlsFiles.readGradeXlsx(fileFinalName);
            basicInfoMapper.readXlsFile(list);
            isSucceed = true;
        } catch(IOException e) {
            e.printStackTrace();
        }
        if (isSucceed) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(1, "操作失败，请联系管理员"));
    }

    // 学生端学分评价页面获取该生的所有已经通过审核的材料
    @RequestMapping("/getAddScoreMaterialDetail")
    @RequiresRoles("student")
    public ResponseEntity getAddScoreMaterialDetail(String year, HttpServletRequest req) {
        String token = req.getHeader("token");
        HashMap<String, Object> redisMap = (HashMap<String, Object>) redisTemplate.opsForValue().get(token);
        String userid = (String) redisMap.get("userid");
        List<HashMap<String, Object>> res = basicInfoMapper.getAddScoreMaterialDetail(userid, year);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 获取所有加分规则
    @RequestMapping("/getRule")
    @RequiresRoles(value = {"manager", "student"}, logical = Logical.OR)
    public ResponseEntity getRule() {
        List<HashMap<String, String>> rules = basicInfoMapper.getRule();
        return ResponseEntity.ok().body(ResultVOUtil.success(rules));
    }

    // 获取某个赛事对应的配置
    @RequestMapping("/getContestConfigById")
    @RequiresRoles("student")
    public ResponseEntity getContestConfigById(String id) {
        HashMap<String, String> config = basicInfoMapper.getContestConfigById(id);
        return ResponseEntity.ok().body(ResultVOUtil.success(config));
    }

    // 获取某个赛事的配置（直接用赛事uuid）
    @RequestMapping("/getContestConfigByUuid")
    @RequiresRoles(value = {"manager", "student"}, logical = Logical.OR)
    public ResponseEntity getContestConfigByUuid(String uuid) {
        String config = basicInfoMapper.getContestConfigByUuid(uuid);
        return ResponseEntity.ok().body(ResultVOUtil.success(config));
    }

    // 获取学业成绩比例
    @RequestMapping("/getGradePercent")
    @RequiresRoles("manager")
    public ResponseEntity getGradePercent() {
        Object res = basicInfoMapper.getGradePercent();
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 修改学业成绩比例
    @RequestMapping("/modifyGradePercent")
    @RequiresRoles("manager")
    public ResponseEntity modifyGradePercent(String percent) {
        basicInfoMapper.modifyGradePercent(Float.parseFloat(percent));
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 获取评价指标列表
    @RequestMapping("getIndexs")
    @RequiresRoles("manager")
    public ResponseEntity getIndexs() {
        List<HashMap<String, String>> res = basicInfoMapper.getIndexs();
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 增加一项指标
    @RequestMapping("/addIndex")
    @RequiresRoles("manager")
    public ResponseEntity addIndex(String name) {
        String uuid = UUID.randomUUID() + "";
        basicInfoMapper.addIndex(uuid, CheckXSSUtil.checkXSS(name));
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 修改指标的名称
    @RequestMapping("/modifyIndex")
    @RequiresRoles("manager")
    public ResponseEntity modifyIndex(String uuid, String name) {
        basicInfoMapper.modifyIndex(CheckXSSUtil.checkXSS(name), uuid);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 删除一项指标
    @RequestMapping("/deleteIndex")
    @RequiresRoles("manager")
    public ResponseEntity deleteIndex(String uuid) {
        basicInfoMapper.deleteIndex(uuid);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 获取专业列表
    @RequestMapping("/getMajors")
    @RequiresRoles("manager")
    public ResponseEntity getMajors() {
        List<HashMap<String, String>> res = basicInfoMapper.getMajors();
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 新增专业
    @RequestMapping("/addMajor")
    @RequiresRoles("manager")
    public ResponseEntity addMajor(String name, String code, String uuid) {
        basicInfoMapper.addMajor(uuid, code, CheckXSSUtil.checkXSS(name));
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 修改专业名称和代码
    @RequestMapping("/modifyMajor")
    @RequiresRoles("manager")
    public ResponseEntity modifyMajor(String uuid, String name, String code) {
        basicInfoMapper.modifyMajor(CheckXSSUtil.checkXSS(name), code, uuid);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 删除一项专业记录
    @RequestMapping("/deleteMajor")
    @RequiresRoles("manager")
    public ResponseEntity deleteMajor(String uuid) {
        basicInfoMapper.deleteMajor(uuid);
        return ResponseEntity.ok().body(ResultVOUtil.successSimple());
    }

    // 管理员端获取图标分析页面数据
    @RequestMapping("/getIndexData")
    @RequiresRoles("manager")
    public ResponseEntity getIndexData(String year, String code) {
        List<HashMap<String, Object>> res = basicInfoMapper.getIndexData(year, code);
        return ResponseEntity.ok().body(ResultVOUtil.success(res));
    }

    // 导出配置文件
    @RequestMapping("/exportConfig")
    @RequiresRoles("manager")
    public ResponseEntity exportConfig(String indicator, String rule, String major) {
        HashMap map = new HashMap();
        if (indicator.equals("1")) {
            String fileName = UUID.randomUUID() + ".xls";
            basicInfoMapper.exportIndexConfig(basePath + "configs\\" + fileName);
            map.put("indicator", fileName);
        }
        if (rule.equals("1")) {
            String fileName = UUID.randomUUID() + ".xls";
            basicInfoMapper.exportRuleConfig(basePath + "configs\\" + fileName);
            map.put("rule", fileName);
        }
        if (major.equals("1")) {
            String fileName = UUID.randomUUID() + ".xls";
            basicInfoMapper.exportMajorConfig(basePath + "configs\\" + fileName);
            map.put("major", fileName);
        }
        return ResponseEntity.ok().body(ResultVOUtil.success(map));
    }

    // 管理员上传学生列表
    @RequestMapping("/addStudents")
    @RequiresRoles("manager")
    public ResponseEntity addStudents(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        // 这里注意一定要用\\.来分割才行，因为在java的正则表达式中，点号表示任意字符
        String[] fileNameParts = fileName.split("\\.");
        String fileFinalName = System.currentTimeMillis() + "." + fileNameParts[fileNameParts.length - 1];
        String filePath = basePath + "students\\";
        File tempFile = new File(filePath, fileFinalName);
        boolean isSucceed = false;
        int res = 0;
        try {
            file.transferTo(tempFile);
            List<HashMap<String, Object>> list = ReadStudentXlsFiles.readStudentXlsx(fileFinalName);
            // 把excel文件存入数据库
            basicInfoMapper.addStudents(list);
            // 为没有设置初始密码的学生设置初始密码
            List<HashMap<String, Object>> stuList = basicInfoMapper.getUnSavedStudentList();
            res = basicInfoMapper.addStudentList(stuList);
            isSucceed = true;
        } catch(IOException e) {
            e.printStackTrace();
        }
        if (isSucceed && res != -1) return ResponseEntity.ok().body(ResultVOUtil.successSimple());
        return ResponseEntity.ok().body(ResultVOUtil.error(1, "操作失败，请联系管理员"));
    }

}