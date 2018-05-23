//package hello.entities;
//
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//
//
//@Entity
//public class Fix {
//
//    @Id
//    @GeneratedValue(strategy=GenerationType.AUTO)
//
//    Long fixID;
//    String status;
//    Integer issueType;
//    String s3Link;
//
//    public Fix (String status, Integer issueType)
//    {
//        this.status = status;
//        this.issueType = issueType;
//    }
//
//
//    public Long getFixID()
//    {
//        return this.fixID;
//    }
////    public void setFixID(Long fixID)
////    {
////        this.fixID = fixID;
////    }
//
//    public String getStatus()
//    {
//        return this.status;
//    }
//    public void setStatus(String status)
//    {
//        this.status = status;
//    }
//
//    public Integer getIssueType()
//    {
//        return this.issueType;
//    }
//    public void setIssueType(Integer issueType)
//    {
//        this.issueType = issueType;
//    }
//
//
//    public String getS3Link()
//    {
//        return this.s3Link;
//    }
//    public void setS3Link(String s3Link)
//    {
//        this.s3Link = s3Link;
//    }
//}
