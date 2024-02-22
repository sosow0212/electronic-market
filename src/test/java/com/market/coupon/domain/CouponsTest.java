package com.market.coupon.domain;

import com.market.coupon.exception.exceptions.UsingAloneCouponContainsException;
import com.market.coupon.infrastructure.ApplyBasicPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.market.coupon.fixture.CouponFixture.쿠픈_생성_독자_사용_할인율_10_퍼센트;
import static com.market.coupon.fixture.CouponFixture.쿠픈_생성_함께_사용_할인금액_10000원;
import static com.market.coupon.fixture.CouponFixture.쿠픈_생성_함께_사용_할인율_20_퍼센트;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class CouponsTest {

    @DisplayName("쿠폰을 적용한다")
    @Nested
    class ApplyCoupon {

        @Test
        void 단독_사용_쿠폰이_다른_쿠폰과_함께_있으면_예외를_발생시킨다() {
            // given
            Coupon usingAloneCoupon = 쿠픈_생성_독자_사용_할인율_10_퍼센트();
            Coupon usingTogetherCoupon = 쿠픈_생성_함께_사용_할인금액_10000원();
            Coupons coupons = new Coupons(List.of(usingAloneCoupon, usingTogetherCoupon));

            // when & then
            assertThatThrownBy(() -> coupons.applyCoupons(10000, new ApplyBasicPolicy()))
                    .isInstanceOf(UsingAloneCouponContainsException.class);
        }

        @Test
        void 쿠폰을_적용한다() {
            // given
            Coupons coupons = new Coupons(List.of(쿠픈_생성_함께_사용_할인율_20_퍼센트(), 쿠픈_생성_함께_사용_할인금액_10000원()));
            int price = 100000;

            // when
            int afterPrice = coupons.applyCoupons(price, new ApplyBasicPolicy());

            // then
            assertThat(afterPrice).isEqualTo(72000);
         }
    }
}
