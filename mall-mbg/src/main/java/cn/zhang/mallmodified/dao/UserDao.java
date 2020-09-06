package cn.zhang.mallmodified.dao;

import cn.zhang.mallmodified.model.User;

public interface UserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    User selectByUsernamePassword(String username,String password);

    String selectQuestionByUsername(String username);

    int checkAnswer(String username,String question,String answer);

    String selectEmailByUsername(String username);

    int updatePassword(String username,String password);

    int checkPassword(String username,String password);

    int checkEmailByUsername(String username,String email);
}