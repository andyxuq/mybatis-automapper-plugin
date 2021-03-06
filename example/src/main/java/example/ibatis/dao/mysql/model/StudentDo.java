package example.ibatis.dao.mysql.model;

import java.util.Date;

public class StudentDo {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_student.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_student.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_student.modified_time
     *
     * @mbggenerated
     */
    private Date modifiedTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_student.user_id
     *
     * @mbggenerated
     */
    private Integer userId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_student.class_name
     *
     * @mbggenerated
     */
    private String className;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_student.id
     *
     * @return the value of ie_student.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_student.id
     *
     * @param id the value for ie_student.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_student.create_time
     *
     * @return the value of ie_student.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_student.create_time
     *
     * @param createTime the value for ie_student.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_student.modified_time
     *
     * @return the value of ie_student.modified_time
     *
     * @mbggenerated
     */
    public Date getModifiedTime() {
        return modifiedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_student.modified_time
     *
     * @param modifiedTime the value for ie_student.modified_time
     *
     * @mbggenerated
     */
    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_student.user_id
     *
     * @return the value of ie_student.user_id
     *
     * @mbggenerated
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_student.user_id
     *
     * @param userId the value for ie_student.user_id
     *
     * @mbggenerated
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_student.class_name
     *
     * @return the value of ie_student.class_name
     *
     * @mbggenerated
     */
    public String getClassName() {
        return className;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_student.class_name
     *
     * @param className the value for ie_student.class_name
     *
     * @mbggenerated
     */
    public void setClassName(String className) {
        this.className = className == null ? null : className.trim();
    }
}