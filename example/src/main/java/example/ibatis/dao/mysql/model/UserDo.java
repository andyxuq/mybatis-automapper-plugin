package example.ibatis.dao.mysql.model;

import java.util.Date;

public class UserDo {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_user.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_user.create_time
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_user.modified_time
     *
     * @mbggenerated
     */
    private Date modifiedTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_user.user_name
     *
     * @mbggenerated
     */
    private String userName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ie_user.user_age
     *
     * @mbggenerated
     */
    private Integer userAge;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_user.id
     *
     * @return the value of ie_user.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_user.id
     *
     * @param id the value for ie_user.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_user.create_time
     *
     * @return the value of ie_user.create_time
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_user.create_time
     *
     * @param createTime the value for ie_user.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_user.modified_time
     *
     * @return the value of ie_user.modified_time
     *
     * @mbggenerated
     */
    public Date getModifiedTime() {
        return modifiedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_user.modified_time
     *
     * @param modifiedTime the value for ie_user.modified_time
     *
     * @mbggenerated
     */
    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_user.user_name
     *
     * @return the value of ie_user.user_name
     *
     * @mbggenerated
     */
    public String getUserName() {
        return userName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_user.user_name
     *
     * @param userName the value for ie_user.user_name
     *
     * @mbggenerated
     */
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ie_user.user_age
     *
     * @return the value of ie_user.user_age
     *
     * @mbggenerated
     */
    public Integer getUserAge() {
        return userAge;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ie_user.user_age
     *
     * @param userAge the value for ie_user.user_age
     *
     * @mbggenerated
     */
    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }
}