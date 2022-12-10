package com.example.stamp_app.service;

import com.example.stamp_app.controller.param.account.LoginPostParam;
import com.example.stamp_app.controller.param.account.RegisterPostParam;
import com.example.stamp_app.controller.response.AccountGetResponse;
import com.example.stamp_app.controller.response.AccountLoginResponse;
import com.example.stamp_app.entity.Account;
import com.example.stamp_app.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.util.DigestUtils.md5DigestAsHex;

@Service
public class AccountService {
    @Autowired
    AccountRepository accountRepository;

    /**
     * アカウント追加Service
     *
     * @param registerPostParam 登録情報
     */
    public void addAccount(RegisterPostParam registerPostParam) {

        boolean isNewUser;

        try {
            isNewUser = accountRepository.findByEmail(registerPostParam.getEmail()) == null;
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!isNewUser) {
            System.out.println(" The email address has already been used.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String hashedPassword = DigestUtils.md5DigestAsHex(registerPostParam.getPassword().getBytes());
        LocalDateTime localDateTime = LocalDateTime.now();
        Account newUser = new Account();
        newUser.setUuid(UUID.randomUUID());
        newUser.setEmail(registerPostParam.getEmail());
        newUser.setPassword(hashedPassword);
        newUser.setCreatedAt(localDateTime);
        newUser.setUpdatedAt(localDateTime);
        System.out.println(newUser);

        try {
            accountRepository.save(newUser);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        System.out.println("Successfully registered.");
    }

    /**
     * ログインService
     *
     * @param loginPostParam ログイン情報
     * @return HttpStatus
     */
    public AccountLoginResponse login(LoginPostParam loginPostParam) {

        boolean isCorrectPassword;

        try {
            Account loginUser = accountRepository.findByEmail(loginPostParam.getEmail());

            // 対象のアカウントが存在しない場合，400を返す
            if (loginUser == null) {
                System.out.println("This account does not exist.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            isCorrectPassword = loginUser.getPassword().matches(md5DigestAsHex(loginPostParam.getPassword().getBytes()));

            // パスワードが合致しない場合，401を返す
            if (!isCorrectPassword) {
                System.out.println("Account Information are not correct.");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            System.out.println("Successfully logged in.");
            return new AccountLoginResponse(HttpStatus.OK, loginUser);

        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * アカウント情報取得Service
     *
     * @param UserUuid ユーザーUUID
     * @return Account アカウント情報
     */
    public AccountGetResponse getAccountInfo(String UserUuid) {

        Account account;

        try {
            account = accountRepository.findByUuid(UUID.fromString(UserUuid));

            // アカウントが存在しない場合，400を返す
            if (account == null) {
                System.out.println("This account does not exist.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            System.out.println(e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // IDと名前のみを返す
        return new AccountGetResponse(account.getId(), account.getName(), account.getCreatedAt(), account.getUpdatedAt());
    }
}
