import app.Models.Users;
import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

public class ArrayListJsonTest {
    public static void main(String[] args) {
        ArrayList<Users> userList = new ArrayList<Users>();
        ArrayList<ArrayList<Users>> teams = new ArrayList<ArrayList<Users>>();
        Users user1 = new Users(1,"Jethro");
        Users user2 = new Users(2, "Anna");

        userList.add(user1);
        userList.add(user2);

        teams.add(userList);

        String jsonStr = JSON.toJSONString(userList);
        System.out.println(jsonStr);

        String jsonStr2 = JSON.toJSONString(teams);
        System.out.println(jsonStr2);

//        List<Users> x = new ArrayList<Users>();
//        x = JSON.parseArray(jsonStr, Users.class);
//        System.out.println(x.getClass());
//        for (Users u:x ) {
//            System.out.println(u.getUserName());
//        }
//
//        Team y = new Team();
//        y = JSON.parseObject(jsonStr2,Team.class);

    }
}
