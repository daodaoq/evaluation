package com.project.evaluation.service.impl;

import com.project.evaluation.entity.MyUser;
import com.project.evaluation.entity.MyUserDetails;
import com.project.evaluation.mapper.AuthorityMapper;
import com.project.evaluation.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    /**
     * 通过用户名加载用户
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser myUser = userMapper.selectByUsername(username);
        if (myUser == null) {
            throw new UsernameNotFoundException(username);
        }
        Integer userId = myUser.getId();
        List<String> authorities = authorityMapper.selectAuthorityByUserId(userId);
        UserDetails userDetails = new MyUserDetails(myUser, authorities);
        return userDetails;
    }
}
