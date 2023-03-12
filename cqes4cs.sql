-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: cqes4cs
-- ------------------------------------------------------
-- Server version	5.7.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `authentication_score`
--

DROP TABLE IF EXISTS `authentication_score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `authentication_score` (
  `id` varchar(28) NOT NULL COMMENT '证明材料的id',
  `score` double(5,2) DEFAULT NULL COMMENT '证明材料能加的分，在教师审核证明材料的时候，系统首先自动进行建议，审核的人可以进行修改',
  `action` tinyint(2) DEFAULT NULL COMMENT '当前的状态，0代表正常状态，1代表学生主动发起了异议请求待审核，2表示教学管理员通过了请求，3表示教学管理员驳回了请求，教学管理员需要进行审核',
  `desca` varchar(200) DEFAULT NULL COMMENT '提出异议的理由',
  `descb` varchar(200) DEFAULT NULL COMMENT '驳回异议的理由',
  `dealcount` tinyint(2) DEFAULT NULL COMMENT '累计处理次数，记录教学管理员累计驳回申请次数（因为驳回之后学生还是可以再次申请）',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='这个表关联学分证明材料表，用来存储证明材';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authentication_score`
--

LOCK TABLES `authentication_score` WRITE;
/*!40000 ALTER TABLE `authentication_score` DISABLE KEYS */;
/*!40000 ALTER TABLE `authentication_score` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authentications`
--

DROP TABLE IF EXISTS `authentications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `authentications` (
  `id` varchar(28) NOT NULL COMMENT '提交的申请的id',
  `name` varchar(50) DEFAULT NULL,
  `checkid` varchar(15) DEFAULT NULL COMMENT '审核材料的用户id，可以根据该id查到对应的用户真实姓名',
  `checktime` varchar(13) DEFAULT NULL COMMENT '审核通过的时间',
  `imgurl` varchar(100) DEFAULT NULL COMMENT '存储在服务器内部的图片的地址，通常就和id一样',
  `state` tinyint(2) DEFAULT NULL COMMENT '审核结果，0表示没有尚未审核，1表示直接通过，2表示修改通过（即辅导员手动做了修改后才提交的审核通过）',
  `year` varchar(10) DEFAULT NULL COMMENT '2019-2020',
  `ruleid` varchar(36) DEFAULT NULL,
  `levelstr` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='学分证明';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authentications`
--

LOCK TABLES `authentications` WRITE;
/*!40000 ALTER TABLE `authentications` DISABLE KEYS */;
/*!40000 ALTER TABLE `authentications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contests`
--

DROP TABLE IF EXISTS `contests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `contests` (
  `id` varchar(13) NOT NULL COMMENT '赛事的唯一id，用时间戳作为唯一标识',
  `title` varchar(50) DEFAULT NULL COMMENT '赛事的标题',
  `content` varchar(1000) DEFAULT NULL COMMENT '赛事的具体内容',
  `state` tinyint(2) DEFAULT '0' COMMENT '赛事是否通过了审核并加入到赛事库。学生提交的赛事需要进行审核，教学管理员提交的赛事不需要进行审核，0代表没有进行审核，1表示已经通过了审核',
  `adder` varchar(15) DEFAULT NULL COMMENT '添加赛事的用户，由学生申请的赛事依旧会保存为学生的id而非教学管理员的id',
  `ruleid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='所有的赛事内容';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contests`
--

LOCK TABLES `contests` WRITE;
/*!40000 ALTER TABLE `contests` DISABLE KEYS */;
INSERT INTO `contests` VALUES ('1660133894341','互联网+','暂无',1,'teacher','fec4b330-c078-4e12-bcfd-d72dc72e72b7'),('1660272616366','计算机设计大赛改名','每两年一次',1,'0123456','fec4b330-c078-4e12-bcfd-d72dc72e72b7');
/*!40000 ALTER TABLE `contests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contests_config`
--

DROP TABLE IF EXISTS `contests_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `contests_config` (
  `uuid` varchar(36) NOT NULL,
  `name` varchar(16) DEFAULT NULL,
  `classconfig` varchar(10000) DEFAULT NULL COMMENT '赛事的级别，0为国家级，1为赛区级（如西南赛区），2为市级，3为校级，4为校职能部门级，5为学部级，保存的应该是一个json字符串',
  `indexid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='赛事配置，根据赛事所属的测评点进行设置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contests_config`
--

LOCK TABLES `contests_config` WRITE;
/*!40000 ALTER TABLE `contests_config` DISABLE KEYS */;
INSERT INTO `contests_config` VALUES ('2b16b88f-83aa-4d06-98ad-e19a08a83c2a','常见的专业赛事','{\"uuid\":\"2b16b88f-83aa-4d06-98ad-e19a08a83c2a\",\"name\":\"常见的专业赛事\",\"children\":[{\"uuid\":\"e459e833-4094-4977-b721-d7379cfe8978\",\"name\":\"个人\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"42e7fbef-fad9-4a0d-b5d7-40c46897362d\",\"name\":\"国家级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"c98f82c4-c921-44e2-b5b1-f4654dcce3c7\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"40\",\"children\":null},{\"uuid\":\"3b5d9840-9ab7-445c-8c19-1530ca6ebb80\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"30\",\"children\":null},{\"uuid\":\"9111d87c-e4a0-47c8-9e43-cb837e865bda\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null}]},{\"uuid\":\"cdbc910b-6572-4de2-b8ec-c7111fea607a\",\"name\":\"省级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"62efa71a-39b9-4655-a928-42b0fc56f4f7\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null},{\"uuid\":\"a16952c4-8dfe-498e-b5e5-584306c9cae4\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"15\",\"children\":null},{\"uuid\":\"bfa4279f-78b3-4ad2-b8a1-ffa573df3baa\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null}]},{\"uuid\":\"ced542fc-878e-410d-8f08-c6404eca16d2\",\"name\":\"校级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"39853771-92fc-4ce0-80b0-6aa137ac1808\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"9508e584-5b96-4161-8bed-6b7069ae5de8\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"7.5\",\"children\":null},{\"uuid\":\"2ab186ed-ce89-4742-a2b0-0aa051f257bc\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null}]},{\"uuid\":\"4f46f43a-41b8-4ad9-aed5-e4d7e5425ad9\",\"name\":\"校职能部门级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"265cc2b5-605e-4086-a79e-04972552ab0e\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"6\",\"children\":null},{\"uuid\":\"d94f51c7-e5ea-4fa1-a825-5f7492af89ed\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"e309fdce-c479-4f3e-a926-f3c592664b36\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}]},{\"uuid\":\"eb9b432e-f5a5-4cdb-8988-44c76535439f\",\"name\":\"学部级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"7e272678-918c-4c4a-80b8-f5459ed46bb3\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null},{\"uuid\":\"77910633-f838-4352-a9a1-cd9654696af7\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"3\",\"children\":null},{\"uuid\":\"c9490884-9e06-4200-bfc6-3c3fcd990023\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"1.5\",\"children\":null}]}]},{\"uuid\":\"adb26faf-637a-4379-b790-54f0ec3e6a0a\",\"name\":\"集体\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"de0a5c54-00b3-4c47-8d8a-d9f9fcc5a972\",\"name\":\"国家级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"0c5e691b-ecd0-4994-8b6b-9d3259c4eeab\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null},{\"uuid\":\"b4c6f4b2-66c3-43c7-acbd-2ce1918ffb92\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"15\",\"children\":null},{\"uuid\":\"d4357c59-aa90-46dd-8ea1-686cdce0cf66\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"3b01e6cd-c01b-4d4c-ace8-19209b54ae97\",\"name\":\"优秀奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null}]},{\"uuid\":\"bf6bee49-3d2d-4b30-81de-3869771dc580\",\"name\":\"省级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"42eaa05e-f51c-4ca8-bdaf-f261f19bb4db\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"934c05ba-6a89-48da-b7ef-64aaddbc1307\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"8\",\"children\":null},{\"uuid\":\"1092abb2-e0a7-4ad1-9cbc-6ef47a1d79f4\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null},{\"uuid\":\"bab88ff2-bc7d-4b40-9877-08c669e6e9c4\",\"name\":\"优秀奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}]},{\"uuid\":\"1faa89b6-2909-4d40-9f12-643fd7ba1ee0\",\"name\":\"校级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"6539c156-4cb9-48d6-89ae-64e13165a578\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"6\",\"children\":null},{\"uuid\":\"3b4d76d0-c7cd-4e06-8070-88f08fe55030\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"7cb64068-3aae-4823-aa6f-c3b6f6cbeacd\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}]},{\"uuid\":\"970044ac-4610-457d-8f78-004c40619b7e\",\"name\":\"校职能部门级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"ce8b5deb-0fab-4b3e-a924-0b2565db95d9\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"3\",\"children\":null},{\"uuid\":\"b4782ca1-074e-4aa9-bd55-2204ef8b44bf\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"b93fe000-9747-4bf3-8884-e0dbd619713c\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"1\",\"children\":null}]},{\"uuid\":\"dd893279-d5c6-42d5-bf76-bcc4bf069383\",\"name\":\"学部级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"be83dcbe-b69f-438f-96ac-2765745463c1\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"d4554faa-3a1f-41d9-a466-709eca612612\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"2125109e-cd49-47ee-a689-6b68566dd093\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"0.5\",\"children\":null}]}]}],\"tag\":\"top\",\"indexid\":\"ad3bf162-8b5e-4d8a-bc89-aefb6255dc27\"}','ad3bf162-8b5e-4d8a-bc89-aefb6255dc27'),('8435872c-7869-4c52-9b85-ffac6f7e7c90','国家英语、计算机等级考试','{\"uuid\":\"8435872c-7869-4c52-9b85-ffac6f7e7c90\",\"name\":\"国家英语、计算机等级考试\",\"children\":[{\"uuid\":\"60ab92af-a520-4340-b26d-14cda59ee7c3\",\"name\":\"英语\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"a52edb64-8164-4071-8034-196ef0882284\",\"name\":\"六级\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"51f35721-890e-4181-8fe1-f4cb008a7961\",\"name\":\"四级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}]},{\"uuid\":\"dc146be2-be96-4f30-b9fc-dd4bf4bb1505\",\"name\":\"计算机二级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}],\"tag\":\"top\",\"indexid\":\"474a1643-d66c-4759-abbb-844cd58471d0\"}','474a1643-d66c-4759-abbb-844cd58471d0'),('fec4b330-c078-4e12-bcfd-d72dc72e72b7','挑战杯','{\"uuid\":\"fec4b330-c078-4e12-bcfd-d72dc72e72b7\",\"name\":\"挑战杯\",\"children\":[{\"uuid\":\"ed13290f-6185-4ca3-ae97-0a77793a013b\",\"name\":\"国家级（挑战杯）\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"cc146339-375d-4212-b6d9-9cadb6e48d1e\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"300\",\"children\":null},{\"uuid\":\"3c294d6b-9b25-4c54-bcd8-a727c128c76c\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"200\",\"children\":null},{\"uuid\":\"77b84946-d6ae-484c-a790-09aeeba41a92\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"150\",\"children\":null},{\"uuid\":\"b100ec30-f929-4516-9509-1e59a4f7d4f8\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"100\",\"children\":null},{\"uuid\":\"589c3e54-f423-4af4-89f8-14fb35fcc3f0\",\"name\":\"优秀奖\",\"tag\":\"root\",\"score\":\"50\",\"children\":null}]},{\"uuid\":\"9b843909-897b-48b2-bbb3-ac8cf558b662\",\"name\":\"省级（挑战杯）\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"2f60a2b6-116f-4cf1-a766-05f57654afd3\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"100\",\"children\":null},{\"uuid\":\"9bcdee6b-ccde-4d8f-8fcf-954dbeee5bd4\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"80\",\"children\":null},{\"uuid\":\"a54444be-4a51-4254-b06b-695258d18a6a\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"60\",\"children\":null},{\"uuid\":\"f6a18574-86b1-4a26-8d46-65a5996cfdc8\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"40\",\"children\":null},{\"uuid\":\"f7da5633-ded1-47c4-a06a-724ed4b155c2\",\"name\":\"优秀奖\",\"tag\":\"root\",\"score\":\"25\",\"children\":null}]},{\"uuid\":\"4bd5cbc6-d285-4a39-8951-107796bfd433\",\"name\":\"校级（含弘杯）\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"007d9a1e-3984-498d-84b4-15c53e2fcd5e\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"50\",\"children\":null},{\"uuid\":\"92da35d2-3111-485c-a48e-74e93df58415\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"40\",\"children\":null},{\"uuid\":\"dcedf3fc-8607-4d20-9058-8e0593f9cfb9\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"30\",\"children\":null},{\"uuid\":\"0e018100-c961-47f9-af03-fde52bb0fa47\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null}]},{\"uuid\":\"bfa6edf9-77a0-45ab-b7d2-6ea6584226cb\",\"name\":\"学部级（志汉杯）\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"8321f4a5-0771-4169-909c-4be63179df17\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"15\",\"children\":null},{\"uuid\":\"e71723f3-19d2-4539-8773-02888a33030f\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"218f32c9-6e15-48db-a175-6f7a5843c0e6\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null},{\"uuid\":\"3a854825-0fba-4510-bfc3-525fd86b9583\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"3\",\"children\":null}]}],\"tag\":\"top\",\"indexid\":\"ad3bf162-8b5e-4d8a-bc89-aefb6255dc27\"}','ad3bf162-8b5e-4d8a-bc89-aefb6255dc27'),('8e00677a-268f-4631-a8ba-f9ec9386b407','学术论文或调查报告获奖','{\"uuid\":\"8e00677a-268f-4631-a8ba-f9ec9386b407\",\"name\":\"学术论文或调查报告获奖\",\"children\":[{\"uuid\":\"ba12245e-d802-4678-9a9b-00cd0b86f2eb\",\"name\":\"国家级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"aae169c5-0505-4faa-89c3-93b1885183dc\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"50\",\"children\":null},{\"uuid\":\"64aad45b-dee3-424a-b042-3bb6e799f8c1\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"40\",\"children\":null},{\"uuid\":\"529b2753-bca4-4f51-9575-31135b4dc885\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"30\",\"children\":null},{\"uuid\":\"eb9e588f-ccec-419d-9e6c-1c9a3d4d7b79\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null}]},{\"uuid\":\"6f4b715b-cd9b-45d5-8313-3b15852ae1d9\",\"name\":\"省级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"e17380c3-7a89-4242-bbde-d758905d37cb\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"25\",\"children\":null},{\"uuid\":\"d27718e9-43cb-448c-a25c-dc4d6fa07eec\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null},{\"uuid\":\"ff36c87f-a8ab-4441-b2d5-ea91000e230c\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"15\",\"children\":null},{\"uuid\":\"77cb7af5-516d-4054-9426-c1709eda80c8\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null}]},{\"uuid\":\"0d929607-e421-4a5f-984d-06c6599105b9\",\"name\":\"校级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"55d08708-0cf0-47fb-af4d-8ded5f260ffa\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"15\",\"children\":null},{\"uuid\":\"4e7ca080-eace-4360-9256-e538ad49be97\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"12\",\"children\":null},{\"uuid\":\"9343c224-bdcb-4a02-bc49-d6fa319d2a7b\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"9\",\"children\":null},{\"uuid\":\"d19bd677-8067-4b50-8a1e-6f66d3305dfd\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"6\",\"children\":null}]},{\"uuid\":\"df6a2a40-961b-4a2a-956b-868788e0f67b\",\"name\":\"学部级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"f0595a6a-1ace-49b4-9ce2-678550642ac2\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"8\",\"children\":null},{\"uuid\":\"9ebbded4-71cc-485b-a8c6-490c243c09bb\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"6\",\"children\":null},{\"uuid\":\"3024615f-dda5-4525-8217-1432525b27bd\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"7cde136a-8e20-46bf-834f-0b1b836e4fca\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}]}],\"tag\":\"top\",\"indexid\":\"ad3bf162-8b5e-4d8a-bc89-aefb6255dc27\"}','ad3bf162-8b5e-4d8a-bc89-aefb6255dc27'),('4ec78a65-8b16-444e-a54b-b7385fd6d693','发表学术论文','{\"uuid\":\"4ec78a65-8b16-444e-a54b-b7385fd6d693\",\"name\":\"发表学术论文\",\"children\":[{\"uuid\":\"82189532-d626-4064-8740-650871301487\",\"name\":\"T级\",\"tag\":\"root\",\"score\":\"120\",\"children\":null},{\"uuid\":\"95f5c281-4d97-4bb5-9ead-5a2d702e8fae\",\"name\":\"A1级\",\"tag\":\"root\",\"score\":\"80\",\"children\":null},{\"uuid\":\"368f0875-58a9-47bc-9011-1c2a0cdd1327\",\"name\":\"A2级\",\"tag\":\"root\",\"score\":\"40\",\"children\":null},{\"uuid\":\"66cbd53e-6a5e-422d-a51b-c6a7c7a95e2e\",\"name\":\"A3级\",\"tag\":\"root\",\"score\":\"30\",\"children\":null},{\"uuid\":\"d5629006-4d08-456b-bede-5ec72b6e7f04\",\"name\":\"B级\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"d8033a20-7153-4bd0-86ee-4239282a9d28\",\"name\":\"C级\",\"tag\":\"root\",\"score\":\"5\",\"children\":null},{\"uuid\":\"5d32d162-88ac-4ddc-9cd2-a9feeaba5044\",\"name\":\"D级\",\"tag\":\"root\",\"score\":\"3\",\"children\":null},{\"uuid\":\"b335732d-e2f8-46f6-bd1f-30aab71bb6d1\",\"name\":\"E级\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"1ed6f501-facc-4a30-a6d2-9734e0ec18d7\",\"name\":\"F级\",\"tag\":\"root\",\"score\":\"0.5\",\"children\":null}],\"tag\":\"top\",\"indexid\":\"7e10a558-79b5-40b1-9d90-1f4cde08b3df\"}','7e10a558-79b5-40b1-9d90-1f4cde08b3df'),('13e0cb69-4e67-469c-8453-496a318118f0','科研项目','{\"uuid\":\"13e0cb69-4e67-469c-8453-496a318118f0\",\"name\":\"科研项目\",\"children\":[{\"uuid\":\"7a5c72e5-a169-457c-a0a8-a140857ce5ce\",\"name\":\"主持\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"34345b28-2ec0-45c8-a622-6a6c3434b983\",\"name\":\"T级\",\"tag\":\"root\",\"score\":\"160\",\"children\":null},{\"uuid\":\"9e28a312-cfa2-4e22-95ee-267838f4efb6\",\"name\":\"A级\",\"tag\":\"root\",\"score\":\"80\",\"children\":null},{\"uuid\":\"5565a051-2ed2-444a-b66d-ba59467d2b0f\",\"name\":\"B级\",\"tag\":\"root\",\"score\":\"40\",\"children\":null},{\"uuid\":\"28d95148-6012-463f-9205-40db586addb0\",\"name\":\"C级\",\"tag\":\"root\",\"score\":\"20\",\"children\":null},{\"uuid\":\"db4a79b4-bdb2-4d88-b4d4-2541ac2c4083\",\"name\":\"D级\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"1e547f3a-dba9-4dbe-98c4-e3ec6430ede1\",\"name\":\"E级\",\"tag\":\"root\",\"score\":\"5\",\"children\":null}]},{\"uuid\":\"be9b510b-7883-4485-9d3f-990dd2888bbb\",\"name\":\"主研\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"c1ebe691-d846-497c-a3f8-c18f7fef9752\",\"name\":\"T级\",\"tag\":\"root\",\"score\":\"16\",\"children\":null},{\"uuid\":\"fd3a176b-787c-4e9e-ab94-709ea557b074\",\"name\":\"A级\",\"tag\":\"root\",\"score\":\"8\",\"children\":null},{\"uuid\":\"8ef8de62-a87e-4769-beae-056647df1a8b\",\"name\":\"B级\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"1e9a3861-fc1a-460d-a2de-d750982f6500\",\"name\":\"C级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"b6a6fa53-2a01-493f-9c1d-6a13ac8dc697\",\"name\":\"D级\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"aa30f173-8953-4bf4-be2b-ca7c565b19e8\",\"name\":\"E级\",\"tag\":\"root\",\"score\":\"0.5\",\"children\":null}]}],\"tag\":\"top\",\"indexid\":\"ad3bf162-8b5e-4d8a-bc89-aefb6255dc27\"}','ad3bf162-8b5e-4d8a-bc89-aefb6255dc27'),('cf07ff6d-75fa-44aa-8521-fcdcf13a5452','学部项目','{\"uuid\":\"cf07ff6d-75fa-44aa-8521-fcdcf13a5452\",\"name\":\"学部项目\",\"children\":[{\"uuid\":\"ecf3dcac-5d23-4d7a-b00a-fda41f659efe\",\"name\":\"重大项目\",\"tag\":\"root\",\"score\":\"15\",\"children\":null},{\"uuid\":\"bd023b35-dc96-4a92-acb6-be772e6e3c2a\",\"name\":\"重点项目\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"21582d17-b9de-44f0-a4fd-40b171349fc9\",\"name\":\"一般项目\",\"tag\":\"root\",\"score\":\"5\",\"children\":null}],\"tag\":\"top\",\"indexid\":\"ad3bf162-8b5e-4d8a-bc89-aefb6255dc27\"}','ad3bf162-8b5e-4d8a-bc89-aefb6255dc27'),('bb229dbd-a190-4d73-abf5-21ac8f89975c','著作类成果（分/部）','{\"uuid\":\"bb229dbd-a190-4d73-abf5-21ac8f89975c\",\"name\":\"著作类成果（分/部）\",\"children\":[{\"uuid\":\"6a664fdf-1402-4c8b-ae41-77fecf79aab0\",\"name\":\"合著\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"eb502782-1c28-4d7c-8783-45d17df95965\",\"name\":\"A级\",\"tag\":\"root\",\"score\":\"20\",\"children\":null},{\"uuid\":\"90912dc8-91f7-470b-84a6-cc4bb5a050af\",\"name\":\"B级\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"60de6288-0773-484d-bc38-ace497d29c05\",\"name\":\"C级\",\"tag\":\"root\",\"score\":\"5\",\"children\":null},{\"uuid\":\"64158624-f328-4881-a8b8-89afce4a8e72\",\"name\":\"D级\",\"tag\":\"root\",\"score\":\"2.5\",\"children\":null}]},{\"uuid\":\"c3b9bb95-2db5-4fd5-b081-4a24dcffa16b\",\"name\":\"主编、副主编\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"fa7b6e19-2ca0-4425-98a9-d3395bfe1324\",\"name\":\"A级\",\"tag\":\"root\",\"score\":\"12\",\"children\":null},{\"uuid\":\"2125ebdd-a2da-4fb3-b277-848a36c79146\",\"name\":\"B级\",\"tag\":\"root\",\"score\":\"8\",\"children\":null},{\"uuid\":\"351f4d02-e605-4ea0-a783-b39962c07324\",\"name\":\"C级\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"580bbc3a-9687-4d1f-b90e-3952f7a7c8d0\",\"name\":\"D级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}]},{\"uuid\":\"66de6785-ee65-412e-8eac-31e908720e9f\",\"name\":\"参著/编\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"e2502f2c-8a5c-409f-b0b3-9c8d1e491daa\",\"name\":\"A级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"cb1868b6-1c17-49d8-ac38-2ed289da0dd9\",\"name\":\"B级\",\"tag\":\"root\",\"score\":\"1.5\",\"children\":null},{\"uuid\":\"81ff2102-8947-488f-b214-b7be0f1faa77\",\"name\":\"C级\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"b89209b1-827d-4f77-a9af-f9d267894901\",\"name\":\"D级\",\"tag\":\"root\",\"score\":\"0.5\",\"children\":null}]}],\"tag\":\"top\",\"indexid\":\"ad3bf162-8b5e-4d8a-bc89-aefb6255dc27\"}','ad3bf162-8b5e-4d8a-bc89-aefb6255dc27'),('eda759ad-d336-4a96-b5bf-fb448456302d','创新创业竞赛','{\"uuid\":\"eda759ad-d336-4a96-b5bf-fb448456302d\",\"name\":\"创新创业竞赛\",\"children\":[{\"uuid\":\"4a346155-75c0-4bea-9466-947b0e21b7be\",\"name\":\"国家级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"3f13985e-a4a0-4a37-9248-6ff7f9019d67\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"300\",\"children\":null},{\"uuid\":\"8ff9d211-4764-446b-aaf6-4265bc24d67c\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"200\",\"children\":null},{\"uuid\":\"09ed057d-9d16-4d64-94a1-35c1d0085ddb\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"150\",\"children\":null},{\"uuid\":\"0b5df562-2e7d-4363-9b5b-621c45d5fcd2\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"100\",\"children\":null},{\"uuid\":\"18173958-5dfc-4072-9ca3-24c27412307d\",\"name\":\"优秀奖\",\"tag\":\"root\",\"score\":\"50\",\"children\":null}]},{\"uuid\":\"5377dd76-1463-4d9f-ab60-15314f74d245\",\"name\":\"省级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"53e9173e-7181-495a-8ce3-bdfacb57ad16\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"100\",\"children\":null},{\"uuid\":\"af84b0b9-0ccb-450c-a0cd-8faee81ecb87\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"80\",\"children\":null},{\"uuid\":\"eb8223ec-0eb9-43f1-ab54-c14b6d543e3e\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"60\",\"children\":null},{\"uuid\":\"2eb166d4-e17c-449a-ae3b-7c6fdabac780\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"40\",\"children\":null},{\"uuid\":\"deac17fb-bf8d-4112-bff2-c3904d22802b\",\"name\":\"优秀奖\",\"tag\":\"root\",\"score\":\"25\",\"children\":null}]},{\"uuid\":\"9b0beb95-fe75-4a76-be12-bdc0c351790b\",\"name\":\"校级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"78e97ad6-4abf-4538-8855-acfa353112ba\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"50\",\"children\":null},{\"uuid\":\"f34acbf9-e73e-4cee-82ef-afccb54854b1\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"40\",\"children\":null},{\"uuid\":\"874764b4-39d6-464b-82de-eeeed88545e6\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"30\",\"children\":null},{\"uuid\":\"fe55ba29-76b9-49f1-a5db-111e396757b3\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null}]},{\"uuid\":\"caf39027-bd6a-4a48-960f-4ed44f428ab0\",\"name\":\"学部级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"1872764b-0a66-457c-a77e-5b6b180a386c\",\"name\":\"特等奖\",\"tag\":\"root\",\"score\":\"15\",\"children\":null},{\"uuid\":\"5820b7e9-4ce1-43b8-ac1f-59f5767ed73c\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"863e47fa-4e6d-486c-beed-d964824ea674\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null},{\"uuid\":\"c3b642d9-1780-47b6-97d1-198653907ad4\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"3\",\"children\":null}]}],\"tag\":\"top\",\"indexid\":\"ad3bf162-8b5e-4d8a-bc89-aefb6255dc27\"}','ad3bf162-8b5e-4d8a-bc89-aefb6255dc27'),('c4abc2b1-34a2-43fd-99c3-2914dc9e227e','大学生创新创业项目','{\"uuid\":\"c4abc2b1-34a2-43fd-99c3-2914dc9e227e\",\"name\":\"大学生创新创业项目\",\"children\":[{\"uuid\":\"ad840d96-e3b6-476d-9cc1-8a95ed6578d2\",\"name\":\"国家级\",\"tag\":\"root\",\"score\":\"60\",\"children\":null},{\"uuid\":\"e4e67e53-e634-4fed-a6a9-f7aa7599fd5f\",\"name\":\"省部级\",\"tag\":\"root\",\"score\":\"30\",\"children\":null},{\"uuid\":\"15f7e521-593e-47b1-96d0-0992d6e516bb\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"15\",\"children\":null}],\"tag\":\"top\",\"indexid\":\"ad3bf162-8b5e-4d8a-bc89-aefb6255dc27\"}','ad3bf162-8b5e-4d8a-bc89-aefb6255dc27'),('244349eb-106e-48ba-9ff7-f0e4c37e2b37','文艺、体育比赛获奖','{\"uuid\":\"244349eb-106e-48ba-9ff7-f0e4c37e2b37\",\"name\":\"文艺、体育比赛获奖\",\"children\":[{\"uuid\":\"7a497bec-7038-4b03-8fdb-f95e9f927cdb\",\"name\":\"个人\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"d1eb6fc0-156e-4e0e-9f59-e9bfc0ac598f\",\"name\":\"国家级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"a6c1d2e4-1072-41bc-ad10-b49611af271f\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"40\",\"children\":null},{\"uuid\":\"9657f29d-936e-4ca1-ad23-d7b56fc729dd\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"30\",\"children\":null},{\"uuid\":\"392afe45-a8a0-41c3-9770-2eda438e3332\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null}]},{\"uuid\":\"7ebc3904-e1bf-410e-8d83-d08dcfa93034\",\"name\":\"省级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"11a91a19-0946-423d-9d61-b1b91ae0d644\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null},{\"uuid\":\"2aa7cfa4-288f-4870-bf31-bb449c6fe3d7\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"15\",\"children\":null},{\"uuid\":\"1e4dfcd9-4c97-4045-8cf0-d0b638560996\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null}]},{\"uuid\":\"a3c89a98-523c-4ef0-9677-a8637f8c2e75\",\"name\":\"校级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"5ad36ac0-6e6e-4b10-b33c-9afea7da14e1\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"1b45344d-7054-449f-b129-4df650b96b64\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"7.5\",\"children\":null},{\"uuid\":\"b2179ad8-4605-49ed-972f-ea066f814ee0\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null}]},{\"uuid\":\"d8d955b0-06d9-422a-8e9f-a58bb30f1d5e\",\"name\":\"校职能部门级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"b906daae-064e-455e-9cbe-35960afa4884\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"6\",\"children\":null},{\"uuid\":\"b65ec2c1-31c4-4f63-b170-db8b1dc6772e\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"54bef820-e4c7-4507-b2a1-8e583e65620a\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}]},{\"uuid\":\"a13174cd-7dbc-4304-ba8d-10135fe93c0c\",\"name\":\"学部级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"ee99a70f-7f83-4ae2-a266-9fc5bbaea5e7\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null},{\"uuid\":\"82ed85c3-901c-4640-8e1f-ca187056092d\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"3\",\"children\":null},{\"uuid\":\"7a0eb521-264c-481a-967f-7e9e37f7beb9\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"1.5\",\"children\":null}]}]},{\"uuid\":\"e27fc09a-8fb5-42ca-8dca-3c167657d3de\",\"name\":\"集体\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"9dd4899b-9f57-42eb-a721-49cc2a818f2f\",\"name\":\"国家级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"f4671ec8-9980-4237-8dc2-73259a728de5\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"20\",\"children\":null},{\"uuid\":\"be8bb834-caaf-4232-bda9-95686d075d2f\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"15\",\"children\":null},{\"uuid\":\"9a7b7392-2675-4fba-ada9-4438b704d4db\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"a4f1e00a-3fd6-4084-ad55-ebfe06919a6e\",\"name\":\"优秀奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null}]},{\"uuid\":\"0c6bf014-deb2-4856-85b2-f12e1c67fbc7\",\"name\":\"省级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"3350155a-132b-46ae-9815-f54d2f45ff40\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"d7a521b3-8d4e-4a5d-b4c0-7a892d24e7d4\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"7.5\",\"children\":null},{\"uuid\":\"6cb166f6-7cf8-4b55-956f-bf664873280d\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"5\",\"children\":null},{\"uuid\":\"13a06ec6-af88-4610-919b-1282f37fc284\",\"name\":\"优秀奖\",\"tag\":\"root\",\"score\":\"2.5\",\"children\":null}]},{\"uuid\":\"d020437c-cab3-43e4-9127-16d9cbde35f8\",\"name\":\"校级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"d71baab4-6533-4c7f-8775-e614110b54fc\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"6\",\"children\":null},{\"uuid\":\"88e8c2a5-dc99-490a-b636-a4c4f126be3b\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"d3237bd8-2a77-4464-8707-37a58d5de17b\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}]},{\"uuid\":\"b839e775-e0f0-4a46-a119-37a02a7f9b13\",\"name\":\"校职能部门级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"2df873c3-2d80-405d-bb11-3cfd3fc68395\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"3\",\"children\":null},{\"uuid\":\"a6c2bd7e-47ca-44bc-96e3-ea5647a293c2\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"2c8fbd70-3264-4c1e-aebc-7ef9c017f98b\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"1\",\"children\":null}]},{\"uuid\":\"6870ffd4-7df1-46ab-a3a0-d6e2019cfe6c\",\"name\":\"学部级\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"d8dfb075-aee6-428a-aecc-fa877237851b\",\"name\":\"一等奖\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"18091a92-0764-4fb0-8cca-6b42cc01278f\",\"name\":\"二等奖\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"6fc91389-ab14-4c54-b9d8-1e6fca0aa370\",\"name\":\"三等奖\",\"tag\":\"root\",\"score\":\"0.5\",\"children\":null}]}]}],\"tag\":\"top\",\"indexid\":\"474a1643-d66c-4759-abbb-844cd58471d0\"}','474a1643-d66c-4759-abbb-844cd58471d0'),('2aee875a-d323-4248-9a24-3e01c609fbf2','社会工作','{\"uuid\":\"2aee875a-d323-4248-9a24-3e01c609fbf2\",\"name\":\"社会工作\",\"children\":[{\"uuid\":\"1a675a44-8e43-4633-9a67-88f586796968\",\"name\":\"学生会正副主席等\",\"tag\":\"root\",\"score\":\"0\",\"children\":null},{\"uuid\":\"f40244e8-2cc1-416b-8303-bfbbe9f6b27e\",\"name\":\"学生会正副部长等\",\"tag\":\"root\",\"score\":\"0\",\"children\":null},{\"uuid\":\"c29cfb2d-65f1-4193-b125-5ddbaf9523c9\",\"name\":\"学生会干事等\",\"tag\":\"root\",\"score\":\"0\",\"children\":null},{\"uuid\":\"4b591bb6-f317-4eaf-a38b-5f6f9d97547d\",\"name\":\"寝室长等\",\"tag\":\"root\",\"score\":\"0\",\"children\":null}],\"tag\":\"top\",\"indexid\":\"7845792b-a68a-4f5a-9f07-5c1465c0860d\"}','7845792b-a68a-4f5a-9f07-5c1465c0860d'),('f8f4e9d8-c940-4e5c-89d1-d47fe80fed4d','五四红旗团委等集体获奖','{\"uuid\":\"f8f4e9d8-c940-4e5c-89d1-d47fe80fed4d\",\"name\":\"五四红旗团委等集体获奖\",\"children\":[{\"uuid\":\"6ce8f523-84f1-4501-85fc-a45c7deae7e0\",\"name\":\"团委副书记等\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"726e6b64-800b-40ef-b122-132526d8f535\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"3\",\"children\":null},{\"uuid\":\"0fc051a4-f71b-4761-aadf-fbae4c089c86\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"6\",\"children\":null},{\"uuid\":\"0e43dc5d-6c41-4a6b-acfe-a4c74b96f027\",\"name\":\"省级\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"24fee184-7d3a-4784-b8a7-d732e3435909\",\"name\":\"国家级\",\"tag\":\"root\",\"score\":\"20\",\"children\":null}]},{\"uuid\":\"e7ae7ef4-1f24-4961-8e0c-7bc08d661874\",\"name\":\"团委正副部长等\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"9ec6838c-7677-47c7-ae35-b52414d9d8f0\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"2a0a008d-3be4-4a5d-935f-7a26adc7ab4b\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"fd38a0b3-27a6-4d80-a362-c726ffb8a80f\",\"name\":\"省级\",\"tag\":\"root\",\"score\":\"8\",\"children\":null},{\"uuid\":\"f0e9cda6-e1e3-4f46-af21-9c4b32cc5295\",\"name\":\"国家级\",\"tag\":\"root\",\"score\":\"15\",\"children\":null}]},{\"uuid\":\"1ae97cdc-8ea6-4498-b11b-72f48bc82f9a\",\"name\":\"团委、学生会干事等\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"39cad4ea-539b-4537-9219-83c6e63be394\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"b81a3fdd-305f-46e2-bb11-2db4b2cbc8a3\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"3\",\"children\":null},{\"uuid\":\"4e79275e-069a-46c5-a9d0-0ff655d2c596\",\"name\":\"省级\",\"tag\":\"root\",\"score\":\"5\",\"children\":null},{\"uuid\":\"1a162fc2-f60e-4c9a-afd4-cacc59ee0b88\",\"name\":\"国家级\",\"tag\":\"root\",\"score\":\"10\",\"children\":null}]}],\"tag\":\"top\",\"indexid\":\"7845792b-a68a-4f5a-9f07-5c1465c0860d\"}','7845792b-a68a-4f5a-9f07-5c1465c0860d'),('466166c3-c149-48b3-9ee3-d31b15e35c25','文明寝室','{\"uuid\":\"466166c3-c149-48b3-9ee3-d31b15e35c25\",\"name\":\"文明寝室\",\"children\":[{\"uuid\":\"d8c0e6ae-6df6-40b2-8d76-5d7074de9c7e\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"4024678d-4a71-47e8-a353-34f8e9730223\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"3\",\"children\":null},{\"uuid\":\"2d0aa3ce-a017-4339-bf4d-d5b762cfbe63\",\"name\":\"省级\",\"tag\":\"root\",\"score\":\"6\",\"children\":null}],\"tag\":\"top\",\"indexid\":\"7845792b-a68a-4f5a-9f07-5c1465c0860d\"}','7845792b-a68a-4f5a-9f07-5c1465c0860d'),('72f5680c-f48c-41b0-a464-a3668601a597','优秀团日活动','{\"uuid\":\"72f5680c-f48c-41b0-a464-a3668601a597\",\"name\":\"优秀团日活动\",\"children\":[{\"uuid\":\"47d6feb3-f2de-469f-b13d-7dd6e6810ec2\",\"name\":\"主要负责人\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"83c1e858-360d-42b5-b698-94fd4a037d70\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"545abf22-d681-4004-827f-a36d4fa8298d\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"3\",\"children\":null}]},{\"uuid\":\"16444c31-123a-42bb-a9b5-85bbd49289cc\",\"name\":\"一般成员\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"5e1e58ea-2be4-45e9-9ae2-3aa7db18d844\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"0.5\",\"children\":null},{\"uuid\":\"e3792a97-287a-4c2c-89f0-36a598abc86e\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"1.5\",\"children\":null}]}],\"tag\":\"top\",\"indexid\":\"7845792b-a68a-4f5a-9f07-5c1465c0860d\"}','7845792b-a68a-4f5a-9f07-5c1465c0860d'),('df82bd96-263c-4739-bd5b-43f21f87cc33','优秀社会实践、优秀活动组织奖','{\"uuid\":\"df82bd96-263c-4739-bd5b-43f21f87cc33\",\"name\":\"优秀社会实践、优秀活动组织奖\",\"children\":[{\"uuid\":\"0d7beaf0-aa80-4144-900d-807d31a92812\",\"name\":\"主要负责人\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"fc102db5-60c5-43be-9962-126cd2aad5c5\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"b4a45101-5f17-43cc-8412-57247bff541a\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"42a01e1c-0431-4fde-9f04-7056e8d77471\",\"name\":\"省级\",\"tag\":\"root\",\"score\":\"8\",\"children\":null},{\"uuid\":\"1f61729a-261e-468b-9774-7fba63b3d3bb\",\"name\":\"国家级\",\"tag\":\"root\",\"score\":\"15\",\"children\":null}]},{\"uuid\":\"dbaf1dd4-6bdf-4858-9a93-cb71ac91e55b\",\"name\":\"一般成员\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"6e466fd7-aca8-4ea5-bf90-43da712a3ea7\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"2e7908d2-b189-4e90-be97-b5530b18b749\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"e5c2475a-efbd-4992-a124-1ea733377327\",\"name\":\"省级\",\"tag\":\"root\",\"score\":\"4\",\"children\":null},{\"uuid\":\"3ee6b3c3-dd9d-4e3a-9818-3421a07dc073\",\"name\":\"国家级\",\"tag\":\"root\",\"score\":\"8\",\"children\":null}]}],\"tag\":\"top\",\"indexid\":\"7845792b-a68a-4f5a-9f07-5c1465c0860d\"}','7845792b-a68a-4f5a-9f07-5c1465c0860d'),('b0ba07c8-ad2f-4fa6-a2a2-a5088fd5cc40','个人荣誉','{\"uuid\":\"b0ba07c8-ad2f-4fa6-a2a2-a5088fd5cc40\",\"name\":\"个人荣誉\",\"children\":[{\"uuid\":\"72bf1af7-c844-4189-ad2f-f6c3ff556f6d\",\"name\":\"优秀党员等\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"8b69f75a-a304-4e17-8ea2-32166284fb49\",\"name\":\"国家级\",\"tag\":\"root\",\"score\":\"60\",\"children\":null},{\"uuid\":\"2ae9937f-f360-4631-b814-bb1b48933511\",\"name\":\"省级\",\"tag\":\"root\",\"score\":\"20\",\"children\":null},{\"uuid\":\"8d063fb7-08de-49ab-9c4f-6e5b3572ab0e\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"6\",\"children\":null},{\"uuid\":\"7a2497ec-c406-4f54-8d9a-1bf1b3eaf0eb\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null}]},{\"uuid\":\"f78d46c9-ba9c-44b7-908d-32ba0eab3d77\",\"name\":\"社会实践先进个人等\",\"tag\":\"normal\",\"children\":[{\"uuid\":\"55ce5044-0b6e-4be9-b505-cac4324e6160\",\"name\":\"国家级\",\"tag\":\"root\",\"score\":\"30\",\"children\":null},{\"uuid\":\"ec69085b-d83d-4b62-9970-0868bcdd5ccd\",\"name\":\"省级\",\"tag\":\"root\",\"score\":\"10\",\"children\":null},{\"uuid\":\"ee01452b-46e0-4f59-a247-2444c3f0279b\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"3\",\"children\":null},{\"uuid\":\"538a3242-a502-4ca4-bce9-71c2825e6c64\",\"name\":\"学部级\",\"tag\":\"root\",\"score\":\"1\",\"children\":null}]}],\"tag\":\"top\",\"indexid\":\"7845792b-a68a-4f5a-9f07-5c1465c0860d\"}','7845792b-a68a-4f5a-9f07-5c1465c0860d'),('2b08d922-86d4-409c-8f51-d3377c803a66','代表学部/学校参加校级/省级以上','{\"uuid\":\"2b08d922-86d4-409c-8f51-d3377c803a66\",\"name\":\"代表学部/学校参加校级/省级以上比赛\",\"children\":[{\"uuid\":\"9dde52cf-80b7-4db9-bcef-dc55fb36cce0\",\"name\":\"校级\",\"tag\":\"root\",\"score\":\"1\",\"children\":null},{\"uuid\":\"61b73eb7-51af-4f54-ad49-f9f9d7518cb5\",\"name\":\"省级\",\"tag\":\"root\",\"score\":\"2\",\"children\":null},{\"uuid\":\"1bc78ab5-7d5c-4444-b95a-f9ecf7616f89\",\"name\":\"国家级\",\"tag\":\"root\",\"score\":\"4\",\"children\":null}],\"tag\":\"top\",\"indexid\":\"ad3bf162-8b5e-4d8a-bc89-aefb6255dc27\"}','ad3bf162-8b5e-4d8a-bc89-aefb6255dc27');
/*!40000 ALTER TABLE `contests_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contests_config_temp`
--

DROP TABLE IF EXISTS `contests_config_temp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `contests_config_temp` (
  `uuid` varchar(36) NOT NULL,
  `name` varchar(16) DEFAULT NULL,
  `classconfig` varchar(10000) DEFAULT NULL,
  `indexid` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contests_config_temp`
--

LOCK TABLES `contests_config_temp` WRITE;
/*!40000 ALTER TABLE `contests_config_temp` DISABLE KEYS */;
/*!40000 ALTER TABLE `contests_config_temp` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grades`
--

DROP TABLE IF EXISTS `grades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `grades` (
  `id` varchar(15) NOT NULL,
  `code` varchar(30) NOT NULL COMMENT '课程代码',
  `year` varchar(10) DEFAULT NULL COMMENT '学年，如 2019-2020',
  `grade` double(5,2) DEFAULT NULL COMMENT '分数，如80',
  `score` double(2,1) DEFAULT NULL COMMENT '学分，如4.0',
  `semester` tinyint(2) DEFAULT NULL COMMENT '学期，1表示上学年，2表示下学年'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grades`
--

LOCK TABLES `grades` WRITE;
/*!40000 ALTER TABLE `grades` DISABLE KEYS */;
/*!40000 ALTER TABLE `grades` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `indexs`
--

DROP TABLE IF EXISTS `indexs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `indexs` (
  `uuid` varchar(36) NOT NULL,
  `name` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `indexs`
--

LOCK TABLES `indexs` WRITE;
/*!40000 ALTER TABLE `indexs` DISABLE KEYS */;
INSERT INTO `indexs` VALUES ('ad3bf162-8b5e-4d8a-bc89-aefb6255dc27','专业比赛'),('7845792b-a68a-4f5a-9f07-5c1465c0860d','社会实践'),('474a1643-d66c-4759-abbb-844cd58471d0','其他赛事');
/*!40000 ALTER TABLE `indexs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `majors`
--

DROP TABLE IF EXISTS `majors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `majors` (
  `uuid` varchar(36) NOT NULL,
  `major_code` varchar(6) DEFAULT NULL COMMENT '专业代码，如：教育技术学，040110',
  `major_name` varchar(20) DEFAULT NULL COMMENT '专业名称',
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='专业及其编号';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `majors`
--

LOCK TABLES `majors` WRITE;
/*!40000 ALTER TABLE `majors` DISABLE KEYS */;
INSERT INTO `majors` VALUES ('d606a998-b445-4799-9976-f7985cc03fb1','040104','教育技术学');
/*!40000 ALTER TABLE `majors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifys`
--

DROP TABLE IF EXISTS `notifys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `notifys` (
  `id` varchar(28) NOT NULL COMMENT '通知的id，由学号+时间戳28位组成，所以由id可以知道时间，不需要另外存储时间',
  `title` varchar(30) DEFAULT NULL COMMENT '通知的标题，如学业预警通知',
  `detail` varchar(200) DEFAULT NULL COMMENT '通知的具体内容，如“王浩同学：您目前处于大四阶段，但是尚有10个专业必修学分不够，请注意把握下学年选课时间，及时选课，避免因为学分不够导致无法拿到毕业证书”',
  `state` tinyint(2) DEFAULT NULL COMMENT '通知是否已读，0表示未读，1表示已读',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='这个通知是系统通知，比如学业警示通知';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifys`
--

LOCK TABLES `notifys` WRITE;
/*!40000 ALTER TABLE `notifys` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifys` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `score_extra`
--

DROP TABLE IF EXISTS `score_extra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `score_extra` (
  `uid` varchar(36) NOT NULL COMMENT 'uuid,36bit',
  `stu_id` varchar(15) DEFAULT NULL COMMENT 'student id:222017321092008',
  `year` varchar(9) DEFAULT NULL COMMENT 'year:2019-2020',
  `score` double(5,2) DEFAULT NULL COMMENT 'the value:2.5',
  `type` tinyint(2) DEFAULT NULL COMMENT 'the type, 0:substract, 1:plus',
  `desc` varchar(100) DEFAULT NULL COMMENT 'the description of every operation',
  PRIMARY KEY (`uid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `score_extra`
--

LOCK TABLES `score_extra` WRITE;
/*!40000 ALTER TABLE `score_extra` DISABLE KEYS */;
/*!40000 ALTER TABLE `score_extra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `scores_all`
--

DROP TABLE IF EXISTS `scores_all`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `scores_all` (
  `userid` varchar(15) NOT NULL COMMENT 'students'' userid',
  `semester` tinyint(2) DEFAULT NULL COMMENT 'what semester does this record belong to. 1 represents the first semester and 4 represents the last semester.',
  `cqa` double(5,2) DEFAULT NULL COMMENT 'the "Ideology and morality" score',
  `cqb` double(5,2) DEFAULT NULL COMMENT 'the "pysical and mental development" score',
  `cqc` double(5,2) DEFAULT NULL COMMENT 'the "acdemic achivement" score',
  `cqd` double(5,2) DEFAULT NULL COMMENT 'the "professional skills" score',
  `cqe` double(5,2) DEFAULT NULL COMMENT 'the "scientific innovation ability" score',
  `cqf` double(5,2) DEFAULT NULL COMMENT 'the "life practice" score'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='all scores records of students'' comprehensive quality.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `scores_all`
--

LOCK TABLES `scores_all` WRITE;
/*!40000 ALTER TABLE `scores_all` DISABLE KEYS */;
/*!40000 ALTER TABLE `scores_all` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_info` (
  `userid` varchar(15) NOT NULL,
  `name` varchar(20) DEFAULT NULL,
  `major_code` varchar(6) DEFAULT NULL,
  `class` varchar(20) DEFAULT NULL,
  `year` varchar(4) DEFAULT NULL COMMENT '所属年级，如2017',
  PRIMARY KEY (`userid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info_manager`
--

DROP TABLE IF EXISTS `user_info_manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user_info_manager` (
  `userid` varchar(15) NOT NULL COMMENT '管理员（包括辅导员和系统管理员的账号信息）',
  `name` varchar(20) DEFAULT NULL COMMENT '管理员的姓名',
  `year` varchar(4) DEFAULT NULL COMMENT '管理的学生的年级，如2017级',
  PRIMARY KEY (`userid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info_manager`
--

LOCK TABLES `user_info_manager` WRITE;
/*!40000 ALTER TABLE `user_info_manager` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_info_manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `users` (
  `userid` varchar(20) NOT NULL,
  `password` varchar(50) DEFAULT NULL,
  `role` varchar(10) DEFAULT NULL COMMENT 'system,manager,student',
  PRIMARY KEY (`userid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('admin','21232F297A57A5A743894A0E4A801FC3','system');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weights_config`
--

DROP TABLE IF EXISTS `weights_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `weights_config` (
  `type` tinyint(2) NOT NULL COMMENT 'typea，即一级指标，如思想品德等',
  `weight` double(3,2) NOT NULL,
  PRIMARY KEY (`type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weights_config`
--

LOCK TABLES `weights_config` WRITE;
/*!40000 ALTER TABLE `weights_config` DISABLE KEYS */;
INSERT INTO `weights_config` VALUES (2,0.20);
/*!40000 ALTER TABLE `weights_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'cqes4cs'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-11-19 14:27:10
