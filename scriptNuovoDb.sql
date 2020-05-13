-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: vivaio_felice
-- ------------------------------------------------------
-- Server version	8.0.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `auto`
--

DROP TABLE IF EXISTS `auto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patente_id` int NOT NULL,
  `carburante_id` int NOT NULL,
  `marca` varchar(45) NOT NULL,
  `modello` varchar(45) NOT NULL,
  `targa` varchar(45) NOT NULL,
  `kw` double NOT NULL,
  `tara` double NOT NULL,
  `data_assicurazione` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `targa_UNIQUE` (`targa`),
  KEY `id_patente_idx` (`patente_id`),
  KEY `id_carb_idx` (`carburante_id`),
  CONSTRAINT `carb` FOREIGN KEY (`carburante_id`) REFERENCES `carburanti` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `id_patente` FOREIGN KEY (`patente_id`) REFERENCES `patenti` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auto`
--

LOCK TABLES `auto` WRITE;
/*!40000 ALTER TABLE `auto` DISABLE KEYS */;
INSERT INTO `auto` VALUES (1,1,1,'Fiat','Panda','DB191YC',50,900,'2020-02-09'),(2,1,3,'Toyota','Yaris','AA547TT',55,920,'2020-02-09'),(3,2,2,'Iveco','Daily','CR878RE',130,2500,'2019-03-15'),(4,1,2,'Citroen','DS3','EL628KG',65,1200,'2019-12-10'),(5,1,1,'Ranault','Clio','FV666CC',80,850,'2019-10-05'),(6,1,1,'Mercedes','Vito','CC789DD',90,1500,'2019-05-05'),(10,1,2,'Peugeot','3006','CA789TY',120,1200,'2020-02-01'),(11,1,2,'Audi','A4','CB458IU',110,1200,'2020-03-01'),(77,1,1,'Toyota','Yaris','TT879TT',100,1000,'2001-01-01'),(80,1,2,'Volvo','V50','AU765UG',100,1000,'2020-04-01'),(81,1,4,'Lamborghini','LamboYO','CH555EF',150,1500,'2020-01-01'),(83,1,1,'Apecar','Apecar','AP333EC',10,100,'2020-04-01'),(84,1,1,'We','We','AA111YY',120,12300,'2020-01-01'),(86,2,4,'Ciao','Ciao','IV888AN',1,8,'2000-01-01');
/*!40000 ALTER TABLE `auto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carburanti`
--

DROP TABLE IF EXISTS `carburanti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carburanti` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipologia` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carburanti`
--

LOCK TABLES `carburanti` WRITE;
/*!40000 ALTER TABLE `carburanti` DISABLE KEYS */;
INSERT INTO `carburanti` VALUES (1,'Benzina'),(2,'Diesel'),(3,'Elettrica'),(4,'Ibrida');
/*!40000 ALTER TABLE `carburanti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `causale`
--

DROP TABLE IF EXISTS `causale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `causale` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descrizione` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `causale`
--

LOCK TABLES `causale` WRITE;
/*!40000 ALTER TABLE `causale` DISABLE KEYS */;
INSERT INTO `causale` VALUES (1,'Manutenzione Ordinaria'),(2,'Manutenzione Straordinaria'),(3,'Prenotazione per lavoro');
/*!40000 ALTER TABLE `causale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dipendenti`
--

DROP TABLE IF EXISTS `dipendenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dipendenti` (
  `id` int NOT NULL AUTO_INCREMENT,
  `livello_id` int NOT NULL,
  `nome` varchar(45) NOT NULL,
  `cognome` varchar(45) NOT NULL,
  `user_name` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_UNIQUE` (`user_name`),
  KEY `id_lv_idx` (`livello_id`),
  CONSTRAINT `id_lv` FOREIGN KEY (`livello_id`) REFERENCES `livelli` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dipendenti`
--

LOCK TABLES `dipendenti` WRITE;
/*!40000 ALTER TABLE `dipendenti` DISABLE KEYS */;
INSERT INTO `dipendenti` VALUES (1,1,'Alberto','Ramponi','albram89','123456'),(2,1,'Marco','Brambilla','marbra97','123456'),(3,1,'Arjuna','Moro','arjmor95','123456'),(4,2,'Valentina','Vastano','valvas','123456'),(5,2,'Antonio','Lezzi','antlez81','123456'),(6,3,'Matteo','Valsasina','matval','123456'),(7,4,'Mario ','Rossi','marros25','123456'),(8,1,'Giuseppe','Verdi','Giuver','123456'),(9,1,'Franco ','Blu','Frablu','123456'),(10,2,'Alessandro','Manzoni','Aleman','123456'),(11,3,'Toto','Cutugno','Totcut','123456'),(12,3,'Giovanni','Giovanni','Giogio','123456'),(13,1,'Stefano','Federico','Stefed','123456'),(14,2,'Valeria','Gambuzza','Valgam','123456'),(24,1,'Beppe','Boppo','bepbop','123456'),(25,1,'Poldo','Giorgiotti','polgio','123456'),(26,1,'Nome','Cognome','nomcom','123456'),(27,1,'Christian','De Sica','chrdes','123456'),(30,1,'Lullo','Lullo','lulul','123456'),(31,3,'Sbrinzo','Sbranzo','sbrisbra','123456'),(32,2,'Cillo','Coppo','cilcop','123456'),(34,1,'Puppa','Rino','puppar','123456'),(38,1,'Baba','Buba','babbub','123456'),(42,2,'Binfo','Bunfo','binbun','123456'),(44,1,'Sasa','Sosa','sassos','123456'),(46,2,'Puppo','Rarra','puprar','123456'),(48,1,'Non','Lo so','nonlos','123456'),(50,2,'Uffa','Chepalle','uffche','123456'),(52,1,'Di nuovo','Che palle','dinche','123456'),(54,1,'Nuova','Prova','nuopro','123456'),(58,2,'Vediamo','Sefunziona','vedsef','123456'),(61,3,'Nuovo','Respo','nuores','123456'),(64,2,'Ultima','Prova','ultpro','123456'),(67,3,'Prova','Patente','propat','123456'),(70,2,'Giorgio','Giurgio','giogiu','123456'),(72,1,'Zio','Caro','ziocar','123456'),(74,1,'Peter','Gabriel','petgab','123456');
/*!40000 ALTER TABLE `dipendenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (268),(268),(268),(268),(268),(268),(268),(268),(268),(268),(268);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `livelli`
--

DROP TABLE IF EXISTS `livelli`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `livelli` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mansione` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `livelli`
--

LOCK TABLES `livelli` WRITE;
/*!40000 ALTER TABLE `livelli` DISABLE KEYS */;
INSERT INTO `livelli` VALUES (1,'Operaio'),(2,'Impiegato'),(3,'Responsabile sede'),(4,'Titolare');
/*!40000 ALTER TABLE `livelli` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifiche`
--

DROP TABLE IF EXISTS `notifiche`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifiche` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dipendente_id` int NOT NULL,
  `descrizione` varchar(250) NOT NULL,
  `conferma` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idd_idx` (`dipendente_id`),
  CONSTRAINT `idd` FOREIGN KEY (`dipendente_id`) REFERENCES `dipendenti` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifiche`
--

LOCK TABLES `notifiche` WRITE;
/*!40000 ALTER TABLE `notifiche` DISABLE KEYS */;
INSERT INTO `notifiche` VALUES (1,1,'Notifica 1',1),(2,1,'Notifica 2',1),(3,2,'Notifica 1 sede 2',1);
/*!40000 ALTER TABLE `notifiche` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parcheggio`
--

DROP TABLE IF EXISTS `parcheggio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parcheggio` (
  `id` int NOT NULL AUTO_INCREMENT,
  `auto_id` int NOT NULL,
  `sede_id` int NOT NULL,
  `data_parch` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_autom_idx` (`auto_id`),
  KEY `id_ssssss_idx` (`sede_id`),
  CONSTRAINT `id_autom` FOREIGN KEY (`auto_id`) REFERENCES `auto` (`id`),
  CONSTRAINT `id_ssssss` FOREIGN KEY (`sede_id`) REFERENCES `sede` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=268 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parcheggio`
--

LOCK TABLES `parcheggio` WRITE;
/*!40000 ALTER TABLE `parcheggio` DISABLE KEYS */;
INSERT INTO `parcheggio` VALUES (1,1,1,'2020-04-30'),(2,1,2,'2020-05-01'),(3,2,1,'2020-04-30'),(4,3,2,'2020-05-01'),(5,4,2,'2020-04-30'),(6,4,1,'2020-05-01'),(7,5,2,'2020-05-01'),(8,6,2,'2020-05-01'),(9,10,1,'2020-05-01'),(10,11,2,'2020-04-30'),(82,11,1,'2020-05-01'),(85,84,2,'2020-04-30'),(87,86,1,'2020-05-01'),(114,2,2,'2020-05-01'),(115,84,1,'2020-05-01'),(117,2,3,'2020-06-01'),(120,4,1,'2020-05-02'),(121,10,1,'2020-05-02'),(122,11,1,'2020-05-02'),(123,86,1,'2020-05-02'),(124,84,1,'2020-05-02'),(125,1,2,'2020-05-02'),(126,3,2,'2020-05-02'),(127,5,2,'2020-05-02'),(128,6,2,'2020-05-02'),(129,2,2,'2020-05-02'),(130,4,1,'2020-05-03'),(131,10,1,'2020-05-03'),(132,11,1,'2020-05-03'),(133,84,1,'2020-05-03'),(134,86,1,'2020-05-03'),(135,1,2,'2020-05-03'),(136,3,2,'2020-05-03'),(137,6,2,'2020-05-03'),(138,2,2,'2020-05-03'),(139,3,2,'2020-05-03'),(140,86,1,'2020-05-04'),(141,10,1,'2020-05-04'),(142,84,1,'2020-05-04'),(143,4,1,'2020-05-04'),(144,11,1,'2020-05-04'),(149,1,2,'2020-05-04'),(150,6,2,'2020-05-04'),(151,2,2,'2020-05-04'),(152,3,2,'2020-05-04'),(153,6,12,'2020-05-05'),(154,10,1,'2020-05-05'),(155,84,1,'2020-05-05'),(156,4,1,'2020-05-05'),(157,11,1,'2020-05-05'),(158,86,1,'2020-05-05'),(159,1,2,'2020-05-05'),(160,2,2,'2020-05-05'),(161,3,2,'2020-05-05'),(167,1,2,'2020-05-06'),(168,2,2,'2020-05-06'),(169,3,2,'2020-05-06'),(178,10,1,'2020-05-06'),(179,84,1,'2020-05-06'),(180,4,1,'2020-05-06'),(181,11,1,'2020-05-06'),(182,86,1,'2020-05-06'),(189,1,2,'2020-05-07'),(190,2,2,'2020-05-07'),(191,3,2,'2020-05-07'),(192,10,1,'2020-05-07'),(193,84,1,'2020-05-07'),(194,4,1,'2020-05-07'),(195,11,1,'2020-05-07'),(196,86,1,'2020-05-07'),(197,10,1,'2020-05-08'),(198,84,1,'2020-05-08'),(199,4,1,'2020-05-08'),(200,11,1,'2020-05-08'),(201,86,1,'2020-05-08'),(202,1,2,'2020-05-08'),(203,2,2,'2020-05-08'),(204,3,2,'2020-05-08'),(205,10,1,'2020-05-09'),(206,84,1,'2020-05-09'),(207,4,1,'2020-05-09'),(208,11,1,'2020-05-09'),(209,86,1,'2020-05-09'),(210,1,2,'2020-05-09'),(211,2,2,'2020-05-09'),(212,3,2,'2020-05-09'),(213,10,1,'2020-05-10'),(214,84,1,'2020-05-10'),(215,4,1,'2020-05-10'),(216,11,1,'2020-05-10'),(217,86,1,'2020-05-10'),(218,1,2,'2020-05-10'),(219,2,2,'2020-05-10'),(220,3,2,'2020-05-10'),(221,10,1,'2020-05-11'),(222,84,1,'2020-05-11'),(223,4,1,'2020-05-11'),(224,11,1,'2020-05-11'),(225,86,1,'2020-05-11'),(226,1,2,'2020-05-11'),(227,3,2,'2020-05-11'),(228,2,2,'2020-05-11'),(235,2,2,'2020-05-12'),(237,6,2,'2020-05-12'),(241,3,2,'2020-05-12'),(242,10,1,'2020-05-12'),(243,84,1,'2020-05-12'),(244,4,1,'2020-05-12'),(245,11,1,'2020-05-12'),(246,86,1,'2020-05-12'),(247,6,2,'2020-05-11'),(249,1,1,'2020-05-12'),(250,10,1,'2020-05-13'),(251,84,1,'2020-05-13'),(252,4,1,'2020-05-13'),(253,11,1,'2020-05-13'),(254,1,1,'2020-05-13'),(255,86,1,'2020-05-13'),(256,2,2,'2020-05-13'),(257,6,2,'2020-05-13'),(258,3,2,'2020-05-13'),(259,6,1,'2020-05-14'),(260,10,1,'2020-05-14'),(261,84,1,'2020-05-14'),(262,4,1,'2020-05-14'),(263,11,1,'2020-05-14'),(264,1,1,'2020-05-14'),(265,86,1,'2020-05-14'),(266,2,2,'2020-05-14'),(267,3,2,'2020-05-14');
/*!40000 ALTER TABLE `parcheggio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patenti`
--

DROP TABLE IF EXISTS `patenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patenti` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipologia` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patenti`
--

LOCK TABLES `patenti` WRITE;
/*!40000 ALTER TABLE `patenti` DISABLE KEYS */;
INSERT INTO `patenti` VALUES (1,'B'),(2,'C1');
/*!40000 ALTER TABLE `patenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `possesso_patenti`
--

DROP TABLE IF EXISTS `possesso_patenti`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `possesso_patenti` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patente_id` int NOT NULL,
  `dipendente_id` int NOT NULL,
  `data_possesso` date NOT NULL,
  `dip_attuale_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_patente_idx` (`patente_id`),
  KEY `id_dip_idx` (`dipendente_id`),
  KEY `FKpvwb3l32juud7e588i9ddg11n` (`dip_attuale_id`),
  CONSTRAINT `FKpvwb3l32juud7e588i9ddg11n` FOREIGN KEY (`dip_attuale_id`) REFERENCES `dipendenti` (`id`),
  CONSTRAINT `id_dip` FOREIGN KEY (`dipendente_id`) REFERENCES `dipendenti` (`id`),
  CONSTRAINT `id_pppppp` FOREIGN KEY (`patente_id`) REFERENCES `patenti` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `possesso_patenti`
--

LOCK TABLES `possesso_patenti` WRITE;
/*!40000 ALTER TABLE `possesso_patenti` DISABLE KEYS */;
INSERT INTO `possesso_patenti` VALUES (1,1,54,'2005-02-01',NULL),(2,1,2,'2019-02-20',NULL),(3,1,3,'2015-05-05',NULL),(4,2,3,'2019-01-02',NULL),(5,1,4,'2000-06-27',NULL),(6,1,5,'2001-07-22',NULL),(7,1,6,'2005-04-08',NULL),(8,2,6,'2005-10-05',NULL),(9,1,7,'1950-07-30',NULL),(10,1,8,'2010-10-10',NULL),(11,1,9,'2020-01-25',NULL),(12,2,9,'2020-01-25',NULL),(13,1,10,'2007-07-07',NULL),(14,1,11,'2006-06-06',NULL),(15,1,12,'2005-05-05',NULL),(16,1,13,'2004-04-04',NULL),(17,2,13,'2008-08-08',NULL),(18,1,14,'2012-12-12',NULL),(19,1,26,'2020-03-13',NULL),(20,1,27,'2020-03-13',NULL),(59,1,58,'1999-12-31',NULL),(62,2,61,'2020-02-01',NULL),(65,1,64,'2018-12-12',NULL),(68,1,67,'1999-05-05',NULL),(76,1,72,'1999-01-01',NULL),(163,2,2,'2020-05-04',NULL);
/*!40000 ALTER TABLE `possesso_patenti` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prenotazioni`
--

DROP TABLE IF EXISTS `prenotazioni`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prenotazioni` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dipendente_id` int NOT NULL,
  `auto_id` int NOT NULL,
  `causale_id` int NOT NULL,
  `data_inizio` datetime NOT NULL,
  `data_fine` datetime DEFAULT NULL,
  `km` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_auto_idx` (`auto_id`),
  KEY `id_dipendente_idx` (`dipendente_id`),
  KEY `id_causale_idx` (`causale_id`),
  CONSTRAINT `id_auto` FOREIGN KEY (`auto_id`) REFERENCES `auto` (`id`),
  CONSTRAINT `id_causale` FOREIGN KEY (`causale_id`) REFERENCES `causale` (`id`),
  CONSTRAINT `id_dipendente` FOREIGN KEY (`dipendente_id`) REFERENCES `dipendenti` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=163 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prenotazioni`
--

LOCK TABLES `prenotazioni` WRITE;
/*!40000 ALTER TABLE `prenotazioni` DISABLE KEYS */;
INSERT INTO `prenotazioni` VALUES (8,1,6,3,'2020-02-10 11:00:00','2020-02-10 18:00:00',0),(9,2,2,3,'2020-02-02 14:00:00','2020-02-02 16:00:00',5430),(10,3,3,3,'2020-02-11 09:00:00','2020-02-11 18:00:00',0),(11,4,1,1,'2019-12-20 09:00:00','2019-12-20 18:00:00',0),(12,4,4,1,'2020-02-20 09:00:00','2020-02-20 18:00:00',0),(13,5,3,2,'2018-08-14 09:00:00','2018-08-16 18:00:00',0),(14,11,6,3,'2019-07-03 09:00:00','2019-07-03 11:00:00',12050),(15,9,6,3,'2019-07-03 11:30:00','2019-07-03 18:00:00',12170),(16,3,11,3,'2020-04-26 11:00:00','2020-04-26 13:00:00',NULL),(92,5,86,3,'2022-02-04 10:00:00','2022-02-04 12:00:00',NULL),(93,1,86,3,'2020-04-26 10:00:00','2020-04-26 12:00:00',10000),(94,1,11,3,'2020-04-27 13:00:00','2020-04-27 15:00:00',12500),(95,4,10,3,'2020-04-26 10:00:00','2020-04-26 12:00:00',65000),(96,4,1,3,'2020-04-26 10:00:00','2020-04-26 12:00:00',50000),(97,4,1,3,'2020-04-26 10:00:00','2020-04-26 12:00:00',50000),(98,2,2,3,'2020-04-26 10:00:00','2020-04-26 12:00:00',120000),(99,4,84,2,'2020-02-05 09:00:00','2020-02-05 18:00:00',NULL),(101,5,6,1,'2022-06-04 09:00:00','2022-06-04 18:00:00',NULL),(103,5,11,2,'2022-06-04 09:00:00','2022-06-04 18:00:00',NULL),(105,5,11,2,'2022-06-04 09:00:00','2022-06-04 18:00:00',NULL),(107,5,3,1,'2020-02-06 10:00:00','2020-02-06 11:00:00',NULL),(109,5,11,2,'2020-05-02 09:00:00','2020-05-02 18:00:00',NULL),(118,6,1,3,'2020-06-02 10:00:00','2020-06-02 11:00:00',NULL),(119,6,2,3,'2020-05-02 09:00:00','2020-05-02 18:00:00',130000),(120,1,10,3,'2020-05-06 09:00:00','2020-05-06 13:00:00',NULL),(162,6,2,3,'2020-05-06 09:00:00','2020-05-06 13:00:00',132000);
/*!40000 ALTER TABLE `prenotazioni` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sede`
--

DROP TABLE IF EXISTS `sede`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sede` (
  `id` int NOT NULL AUTO_INCREMENT,
  `regione` varchar(45) NOT NULL,
  `citta` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sede`
--

LOCK TABLES `sede` WRITE;
/*!40000 ALTER TABLE `sede` DISABLE KEYS */;
INSERT INTO `sede` VALUES (1,'Lombardia','Milano'),(2,'Lombardia','Como'),(3,'Piemonte','Novara'),(4,'Molise','Campobasso'),(5,'Sicilia','Enna'),(6,'Calabria','Cutro'),(7,'Puglia','Brindisi'),(8,'Veneto','Verona'),(9,'Lazio','Roma'),(10,'Emilia','Bologna'),(11,'Toscana','Firenze'),(12,'Val d\'Aosta','Aosta');
/*!40000 ALTER TABLE `sede` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sede_dip`
--

DROP TABLE IF EXISTS `sede_dip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sede_dip` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dipendente_id` int NOT NULL,
  `sede_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_ddddd_idx` (`dipendente_id`),
  KEY `id_seseseeses_idx` (`sede_id`),
  CONSTRAINT `id_ddddd` FOREIGN KEY (`dipendente_id`) REFERENCES `dipendenti` (`id`),
  CONSTRAINT `id_seseseeses` FOREIGN KEY (`sede_id`) REFERENCES `sede` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sede_dip`
--

LOCK TABLES `sede_dip` WRITE;
/*!40000 ALTER TABLE `sede_dip` DISABLE KEYS */;
INSERT INTO `sede_dip` VALUES (1,1,1),(2,2,2),(3,3,1),(4,4,2),(5,5,1),(6,6,2),(7,7,1),(8,8,2),(9,9,1),(10,10,2),(11,11,1),(12,12,2),(13,13,1),(14,14,3),(15,24,2),(16,25,1),(17,26,2),(18,27,2),(33,32,1),(35,34,1),(39,38,1),(43,42,1),(45,44,1),(47,46,1),(49,48,1),(51,50,1),(53,52,1),(55,54,1),(60,58,1),(63,61,2),(66,64,2),(69,67,1),(71,70,1),(73,72,1),(75,74,1);
/*!40000 ALTER TABLE `sede_dip` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spesa_manutenzione`
--

DROP TABLE IF EXISTS `spesa_manutenzione`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spesa_manutenzione` (
  `id` int NOT NULL AUTO_INCREMENT,
  `auto_id` int NOT NULL DEFAULT '1',
  `spesa` double DEFAULT NULL,
  `data_spesa` date DEFAULT NULL,
  `descrizione` varchar(45) NOT NULL DEFAULT 'DEF',
  `dettaglio` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_a_idx` (`auto_id`),
  CONSTRAINT `id_a` FOREIGN KEY (`auto_id`) REFERENCES `auto` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spesa_manutenzione`
--

LOCK TABLES `spesa_manutenzione` WRITE;
/*!40000 ALTER TABLE `spesa_manutenzione` DISABLE KEYS */;
INSERT INTO `spesa_manutenzione` VALUES (6,1,1570.44,'2020-02-09','Riparazione carrozzeria',NULL),(7,1,230.5,'2019-12-05','Tagliando',NULL),(8,1,40,'2019-02-03','Lampadina sostituita',NULL),(9,2,430.8,'2019-05-03','Aria condizionata',NULL),(10,3,150,'2019-04-04','Batteria',NULL),(11,3,140.3,'2019-12-05','Tagliando',NULL),(100,84,666,'2020-05-07','Non si accende','Abbiamo perso Twinky'),(102,6,NULL,NULL,'Tagliando',NULL),(104,11,NULL,NULL,'Boh',NULL),(106,11,NULL,NULL,'Boh',NULL),(108,3,NULL,NULL,'Blabla',NULL),(110,11,NULL,NULL,'E\' esplosa',NULL);
/*!40000 ALTER TABLE `spesa_manutenzione` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-05-13  9:29:34
