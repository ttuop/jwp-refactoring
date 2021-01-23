package kitchenpos.menu.application;

import static kitchenpos.utils.TestFixture.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import kitchenpos.BaseServiceTest;
import kitchenpos.menu.dto.MenuGroupRequest;
import kitchenpos.menu.dto.MenuGroupResponse;

public class MenuGroupServiceTest extends BaseServiceTest {
	@Autowired
	private MenuGroupService menuGroupService;

	@Test
	@DisplayName("메뉴그룹을 등록할 수 있다.")
	void create() {
		//given
		MenuGroupRequest menuGroupRequest = new MenuGroupRequest(메뉴그룹_신규_NAME);

		//when
		MenuGroupResponse result = menuGroupService.create(menuGroupRequest);

		//then
		assertThat(result.getId()).isNotNull();
		assertThat(result.getName()).isEqualTo(메뉴그룹_신규_NAME);
	}
}