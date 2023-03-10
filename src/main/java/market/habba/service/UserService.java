package market.habba.service;

import market.habba.entity.User;
import market.habba.model.UserRequestDto;

public interface UserService {
    User getUserByEmail(String email);

    void addNewUser(UserRequestDto userRequestDto);
}
