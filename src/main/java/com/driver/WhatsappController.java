package com.driver;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("whatsapp")
public class WhatsappController {

    //Autowire will not work in this case, no need to change this and add autowire
    WhatsappService whatsappService = new WhatsappService();

    @PostMapping("/add-user")
    public String createUser(@RequestParam("name") String name, @RequestParam("mobile") String mobile) throws Exception {
        //If the mobile number exists in database, throw "User already exists" exception
        //Otherwise, create the user and return "SUCCESS"

        return whatsappService.createUser(name, mobile);
    }

    @PostMapping("/add-group")
    public Group createGroup(@RequestParam("users") List<User> users){
       return whatsappService.createGroup(users);
    }

    @PostMapping("/add-message")
    public int createMessage(String content){
      return whatsappService.createMessage(content);
    }

    @PutMapping("/send-message")
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        return whatsappService.sendMessage(message, sender, group);
    }
    @PutMapping("/change-admin")
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        return whatsappService.changeAdmin(approver, user, group);
    }

    @DeleteMapping("/remove-user")
    public int removeUser(User user) throws Exception{
//        This is a bonus problem and does not contains any marks
//        A user belongs to exactly one group
//        If user is not found in any group, throw "User not found" exception
//        If user is found in a group and it is the admin, throw "Cannot remove admin" exception
//        If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
//        If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)

        return whatsappService.removeUser(user);
    }

//    @GetMapping("/find-messages")
//    public String findMessage(Date start, Date end, int K) throws Exception{
        //This is a bonus problem and does not contains any marks
        // Find the Kth latest message between start and end (excluding start and end)
        // If the number of messages between given time is less than K, throw "K is greater than the number of messages" exception

//        return whatsappService.findMessage(start, end, K);
//    }
}
