package com.market.coupon.infrastructure;

import com.market.coupon.domain.MemberCoupon;
import com.market.coupon.domain.MemberCouponRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberCouponFakeRepository implements MemberCouponRepository {

    private final Map<Long, MemberCoupon> map = new HashMap<>();

    private Long id = 0L;

    @Override
    public List<MemberCoupon> findAllByMemberId(final Long memberId) {
        return map.values().stream()
                .filter(memberCoupon -> memberCoupon.getMemberId().equals(memberId))
                .toList();
    }

    @Override
    public MemberCoupon save(final MemberCoupon memberCoupon) {
        id++;

        MemberCoupon saved = MemberCoupon.builder()
                .id(id)
                .memberId(memberCoupon.getMemberId())
                .couponId(memberCoupon.getCouponId())
                .build();

        map.put(id, saved);
        return saved;
    }

    @Override
    public void deleteByMemberIdAndCouponIdIn(final Long memberId, final List<Long> couponIds) {
        List<MemberCoupon> memberCoupons = map.values().stream()
                .filter(memberCoupon -> memberCoupon.getMemberId().equals(memberId) && couponIds.contains(memberCoupon.getCouponId()))
                .toList();

        memberCoupons.forEach(memberCoupon -> {
            map.remove(memberCoupon.getId());
        });
    }

    @Override
    public int countMemberIdWithCouponIds(final Long memberId, final List<Long> couponIds) {
        List<MemberCoupon> memberCoupons = map.values().stream()
                .filter(it -> it.getMemberId().equals(memberId))
                .toList();

        return (int) memberCoupons.stream()
                .filter(it -> couponIds.contains(it.getCouponId()))
                .count();
    }
}
