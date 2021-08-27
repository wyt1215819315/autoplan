package com.netmusic.service;

import com.misec.utils.HelpUtil;
import com.netmusic.dao.AutoNetmusicDao;
import com.netmusic.model.AutoNetmusic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NetmusicService {

    @Autowired
    private AutoNetmusicDao netmusicDao;

    public List<AutoNetmusic> getAllPlan(){
        List<AutoNetmusic> autoNetmusics = netmusicDao.selectAll();
        List<AutoNetmusic> result = new ArrayList<>();
        for (AutoNetmusic autoNetmusic : autoNetmusics) {
            autoNetmusic.setPassword(null);
            autoNetmusic.setNetmusicId(null);
            autoNetmusic.setPhone(null);
            autoNetmusic.setNetmusicName(HelpUtil.userNameEncode(autoNetmusic.getNetmusicName()));
            result.add(autoNetmusic);
        }
        return result;
    }

}
