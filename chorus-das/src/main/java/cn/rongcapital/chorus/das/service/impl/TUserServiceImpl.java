package cn.rongcapital.chorus.das.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.rongcapital.chorus.das.dao.TUserMapper;
import cn.rongcapital.chorus.das.entity.TUser;
import cn.rongcapital.chorus.das.service.TUserService;

@Service
public class TUserServiceImpl implements TUserService {

    @Autowired
    private TUserMapper tUserMapper;
    
    @Override
    public TUser getUserById(String userId) {
        return tUserMapper.getUserById(userId);
    }

    @Override
    public int add(TUser user) {
        return tUserMapper.add(user);
    }

    @Override
    public void replaceBatch(List<TUser> users) {
        tUserMapper.replaceBatch(users);
    }

}
