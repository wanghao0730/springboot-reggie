package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.common.BaseContext;
import com.itheima.common.Result;
import com.itheima.entity.AddressBook;
import com.itheima.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     *  获取用户的所有收货地址 有个问题这里是从线程中取用户(前面登录有保存到ThreadLocal中) 还可以让前台传入用户的id来查询
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> getAddress() {
        //当前用户的id
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,currentId);
        //查询用户的所有地址
        List<AddressBook> list = addressBookService.list(queryWrapper);

        return Result.success(list);
    }


    @PutMapping("/default")
    public Result<AddressBook> updateDefaultAddr(@RequestBody AddressBook addressBook) {

        //从线程中取当前用户将当前用户下的所有地址的status值进行修改
        Long currentId = BaseContext.getCurrentId();

        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,currentId);
        //添加设置值
        //update address_book set is_default = 0 where user_id = ?
        updateWrapper.set(AddressBook::getIsDefault,0); //全部设置成不是默认

        //进行更新
        addressBookService.update(updateWrapper);

        //设置默认值
        addressBook.setIsDefault(1);
        //修改当前默认值根据id
        addressBookService.updateById(addressBook);

        return Result.success(addressBook);

    }
}
