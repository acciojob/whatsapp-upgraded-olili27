package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<User>> personalChatMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Group, List<Message>> personalChatMessageMap;
    private HashMap<User, List<Message>> senderMap;
    private HashMap<Group, User> adminMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<>();
        this.groupUserMap = new HashMap<>();
        this.personalChatMap = new HashMap<>();
        this.personalChatMessageMap = new HashMap<>();
        this.senderMap = new HashMap<>();
        this.adminMap = new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public HashMap<Group, List<Message>> getPersonalChatMessageMap() {
        return personalChatMessageMap;
    }

    public HashMap<Group, List<User>> getGroupUserMap() {
        return groupUserMap;
    }

    public HashMap<Group, List<User>> getPersonalChatMap() {
        return personalChatMap;
    }

    public HashMap<Group, List<Message>> getGroupMessageMap() {
        return groupMessageMap;
    }

    public HashMap<User, List<Message>> getSenderMap() {
        return senderMap;
    }

    public HashMap<Group, User> getAdminMap() {
        return adminMap;
    }

    public int getCustomGroupCount() {
        return customGroupCount;
    }

    public int getMessageId() {
        return messageId;
    }

    public Group createGroup(List<User> users) {
        Group group;

        if (users.size() == 2) {
            group = new Group(users.get(1).getName(), 2);
            personalChatMap.put(group, users);

            // get admin
            adminMap.put(group, users.get(0));
        } else {
            this.customGroupCount += 1;

            group = new Group("Group " + this.customGroupCount, users.size());
            groupUserMap.put(group, users);

            // get admin
            adminMap.put(group, users.get(0));
        }

        return group;
    }

    public int createMessage(String content) {
        this.messageId += 1;
        Message newMessage = new Message(this.messageId, content);

        return this.messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception {
        // check if group exists and determine the group to send to
            if (personalChatMap.containsKey(group)) {

                // check if user is a member of the group
                    if (personalChatMap.get(group).contains(sender)) {
                        List<Message> messageList = personalChatMessageMap.getOrDefault(group, new ArrayList<>());
                        messageList.add(message);
                        personalChatMessageMap.put(group, messageList);

                        List<Message> sendersMessages = senderMap.getOrDefault(sender, new ArrayList<>());
                        sendersMessages.add(message);
                        senderMap.put(sender, sendersMessages);

                        return personalChatMessageMap.get(group).size();
                    }
                else {
                    throw new Exception("You are not allowed to send message");
                }

            } else if (groupUserMap.containsKey(group)) {

                // check if user is a member of the group
                    if (groupUserMap.get(group).contains(sender)) {
                        List<Message> messageList = groupMessageMap.getOrDefault(group, new ArrayList<>());
                        messageList.add(message);
                        groupMessageMap.put(group, messageList);

                        List<Message> sendersMessages = senderMap.getOrDefault(sender, new ArrayList<>());
                        sendersMessages.add(message);
                        senderMap.put(sender, sendersMessages);

                        return groupMessageMap.get(group).size();
                    }
                else {
                    throw new Exception("You are not allowed to send message");
                }

            }

            throw new Exception("Group does not exist");
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        // check if group exists and determine the type of group
            if (personalChatMap.containsKey(group)) {
                    if (personalChatMap.get(group).contains(user)) {  // check if user is a member of the group
                            if (adminMap.get(group) == approver) { // check if approver is the admin of the group
                                adminMap.put(group, user);
                                return "SUCCESS";
                            }
                            else {
                            return "Approver does not have rights";
                        }
                    }
                else {
                    return "User is not a participant";
                }
            } else if (groupUserMap.containsKey(group)) {

                // check if user is a member of the group
                    if (groupUserMap.get(group).contains(user)) {
                            if (adminMap.get(group) == approver) {
                                adminMap.put(group, user);
                                return "SUCCESS";
                            }
                        else {
                           return "Approver does not have rights";
                        }
                    }
                 else {
                    return "User is not a participant";
                }
            }

            return ("Group does not exist");
    }

    public int removeUser(User user) throws Exception {
        int count = 0;
        
        if(!containsUser(groupUserMap, user) && !containsUser(personalChatMap, user)) throw new Exception("User not found");

        if (adminMap.containsValue(user)) {
            throw new Exception("Cannot remove admin");
        }
        
        if (containsUser(groupUserMap, user)) {
            Group group = getGroup(groupUserMap, user);
            List<User> userList = getListWhereUserBelongs(groupUserMap, user);
            userList.remove(user);
            group.setNumberOfParticipants(userList.size());

            count += group.getNumberOfParticipants();

            groupUserMap.put(group, userList);

            List<Message> messagesForTheGroup = groupMessageMap.get(group);
            List<Message> messagesForTheUser = senderMap.get(user);

            for (Message message: messagesForTheUser) {
                messagesForTheGroup.remove(message);
            }

            count += messagesForTheGroup.size();

            senderMap.remove(user);
            groupMessageMap.put(group, messagesForTheGroup);

            for (User user1: senderMap.keySet()) {
                count += senderMap.get(user1).size();
            }

        } else if (containsUser(personalChatMap, user)) {
            Group group = getGroup(personalChatMap, user);
            List<User> userList = getListWhereUserBelongs(personalChatMap, user);
            userList.remove(user);
            group.setNumberOfParticipants(userList.size());

            count += group.getNumberOfParticipants();

            personalChatMap.put(group, userList);

            List<Message> messagesForTheGroup = personalChatMessageMap.get(group);
            List<Message> messagesForTheUser = senderMap.get(user);

            for (Message message: messagesForTheUser) {
                messagesForTheGroup.remove(message);
            }

            count += messagesForTheGroup.size();

            senderMap.remove(user);
            personalChatMessageMap.put(group, messagesForTheGroup);

            for (User user1: senderMap.keySet()) {
                count += senderMap.get(user1).size();
            }
        }

       return count;
    }

    private Group getGroup(HashMap<Group, List<User>> db, User user) {
        Group group = null;

        for (Map.Entry<Group, List<User>> groupUserEntry: db.entrySet()) {
            if (groupUserEntry.getValue().contains(user)) group = groupUserEntry.getKey();
        }

        return group;
    }

    private List<User> getListWhereUserBelongs(HashMap<Group, List<User>> db, User user) {
        List<User> userList = null;

        for (Map.Entry<Group, List<User>> groupUserEntry: db.entrySet()) {
            if (groupUserEntry.getValue().contains(user)) userList = groupUserEntry.getValue();
        }

        return userList;
    }

    private boolean containsUser(HashMap<Group, List<User>> db, User user) {
        for (Map.Entry<Group, List<User>> groupUserEntry: db.entrySet()) {
            if (groupUserEntry.getValue().contains(user)) return true;
        }
        
        return false;
    }

}

// TODO exception handling
