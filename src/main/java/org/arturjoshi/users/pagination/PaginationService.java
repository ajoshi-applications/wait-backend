package org.arturjoshi.users.pagination;

import org.arturjoshi.users.domain.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaginationService {

    public Page<User> getPage(List<User> users, Integer pageNumber, Integer pageSize) {
        Page<User> resultPage = new Page<>();

        /* Number of elements */
        resultPage.setTotalElements(users.size());
        resultPage.setSize(pageSize);
        resultPage.setNumber(pageNumber);

        /* Total pages */
        if (resultPage.getTotalElements() == 0) {
            resultPage.setTotalPages(0);
            resultPage.setFirst(true);
            resultPage.setLast(true);
            resultPage.setContent(new ArrayList<>());
            resultPage.setNumberOfElements(0);
            return resultPage;
        } else {
            resultPage.setTotalPages(((resultPage.getTotalElements() - 1) / pageSize) + 1);
            if(pageNumber > resultPage.getTotalPages()) {
                return getPage(users, resultPage.getTotalPages(), pageSize);
            }
            resultPage.setFirst(pageNumber == 1);
            resultPage.setLast(pageNumber.equals(resultPage.getTotalPages()));
            resultPage.setNumberOfElements(
                    resultPage.getTotalElements() / (pageSize * pageNumber) >= 1 ?
                        pageSize : resultPage.getTotalElements() % pageSize);
            if (resultPage.isLast()) {
                resultPage.setContent(users.subList((pageNumber - 1) * pageSize, resultPage.getTotalElements()));
            } else {
                resultPage.setContent(users.subList((pageNumber - 1) * pageSize, pageNumber * pageSize));
            }
            return resultPage;
        }
    }
}
