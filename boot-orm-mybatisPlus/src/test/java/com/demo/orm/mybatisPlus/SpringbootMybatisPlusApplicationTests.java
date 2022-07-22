package com.demo.orm.mybatisPlus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.demo.orm.mybatisPlus.mapper.UserMapper;
import com.demo.orm.mybatisPlus.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class SpringbootMybatisPlusApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList;
        userList = userMapper.selectList(null);
//        Assert.assertEquals(6, userList.size());
        userList.forEach(System.out::println);
    }

    @Test
    public void testInsert(){
        User user = new User();
        user.setId(7l);
        user.setAge(20);
        user.setName("测试员");
        int count = userMapper.insert(user);
        System.out.println( "插入记录数：" + count );
    }

    @Test
    public void selectById(){
        User user = userMapper.selectById(6);
        System.out.println(user);
    }

    @Test
    public void selectByMap(){
        Map<String, Object> map = new HashMap<>();

        // key 存储的是数据库中列名，不是类的属性名
        map.put("id", 6);
        List<User> user = userMapper.selectByMap(map);
        System.out.println("=======================");
        System.out.println( user.get(0) );
        System.out.println("=======================");
    }

    /**
     * 使用 queryWrapper 构建查询条件
     */
    @Test
    public void selectByWrapper(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        QueryWrapper<User> query = Wrappers.query();

        // 这里的 colunm 指的数据库中的列，不是实体的属性
        queryWrapper.gt("age", "18");
//        userMapper.selectList(Wrappers.<User>lambdaQuery().gt(User::getAge, 25));     // 可以使用 lambda表达式这样子构建

        List<User> users = userMapper.selectList(queryWrapper);
        System.out.println("=======================");
        users.forEach(System.out::println);
        System.out.println("=======================");
    }


    /**
     * 使用 condition 来构建查询的 Wrapper
     */
    @Test
    public void testCondition(){
        selectByCondition("J", "");
    }


    public void selectByCondition(String name, String email){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        // 最原始的写法
//        if (StringUtils.isNotEmpty(name)) {
//            queryWrapper.like("name", name);
//        }
//
//        if (StringUtils.isNotEmpty(email)) {
//            queryWrapper.like("email", email);
//        }

        // 使用 mp 的 condition 改写，由于 第一个参数是 boolean，如果为 true 的时候才会添加到查询条件上面
        queryWrapper.like(StringUtils.isNotEmpty(name), "name", name)
                    .like(StringUtils.isNotEmpty(email), "email", email);

        List<User> users = userMapper.selectList(queryWrapper);
        System.out.println("=======================");
        users.forEach(System.out::println);
        System.out.println("=======================");
    }

    /**
     * 在 Mapper 接口中写入注解形式的 SQL
     */
    @Test
    public void selectByCustomSQL(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like("name", "J");
        queryWrapper.gt("age", "18");
        List<User> users = userMapper.selectCustomSql(queryWrapper);
        System.out.println("=======================");
        users.forEach(System.out::println);
        System.out.println("=======================");
    }

    /**
     * 在 Mapper 接口中写入注解形式的 SQL
     */
    @Test
    public void selectByCustomSQLByXML(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.like("name", "J");
        queryWrapper.gt("age", "18");
        List<User> users = userMapper.selectCustomSqlByXML(queryWrapper);
        System.out.println("=======================");
        users.forEach(System.out::println);
        System.out.println("=======================");
    }

    @Test
    public void selectPage(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("age", "18");

        // 最后的 isSearchCount 表示是否查询总记录数
        Page<User> page = new Page<>(1, 2, false);



        IPage<User> iPage = userMapper.selectPage(page, queryWrapper);
        System.out.println("=======================");
        System.out.println("总记录数："+ iPage.getTotal());
        System.out.println("分页数:" + iPage.getPages());

        List<User> users = iPage.getRecords();
        users.forEach(System.out::println);
        System.out.println("=======================");
    }

    @Test
    public void selectMapsPage(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("age", "18");

        // 最后的 isSearchCount 表示是否查询总记录数
        Page<Map<String, Object>> page = new Page<>(1, 2, true);

        IPage<Map<String, Object>> iPage = userMapper.selectMapsPage(page, queryWrapper);
        System.out.println("=======================");
        System.out.println("总记录数："+ iPage.getTotal());
        System.out.println("分页数:" + iPage.getPages());

        List<Map<String, Object>> users = iPage.getRecords();
        for (Map user : users) {
            System.out.println(user.toString());
        }
        System.out.println("=======================");
    }

    @Test
    public void selectCustomPage(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.gt("age", "18");

        // 最后的 isSearchCount 表示是否查询总记录数
        Page<User> page = new Page<>(1, 2, false);

        IPage<User> iPage = userMapper.selectCustomPage(page, queryWrapper);
        System.out.println("=======================");
        System.out.println("总记录数："+ iPage.getTotal());
        System.out.println("分页数:" + iPage.getPages());

        List<User> users = iPage.getRecords();
        users.forEach(System.out::println);
        System.out.println("=======================");
    }
}
