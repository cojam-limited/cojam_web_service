package io.cojam.web.klaytn.dto;

import io.cojam.web.klaytn.service.AssertionConcern;
import io.cojam.web.utils.Converter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.web3j.utils.Strings;

import javax.persistence.Column;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Address extends AssertionConcern {
    @Column(nullable = false)
    private String value;

    public Address(String value) {
        this.assertArgumentNotNull(value, "value must not be null");
        this.assertArgumentLength(value, 42, 66, "length of address string is out of range: " + value.trim().length());

        String without0x = Converter.remove0x(value.toLowerCase());

        this.assertArgumentEquals(
                without0x.substring(0, without0x.length() - 40),
                Strings.zeros(without0x.length() - 40),
                "value must be padded with 0"
        );
        this.value = "0x" + without0x.substring(without0x.length() - 40);
    }

    public byte[] toBytes() {
        return Converter.hexStringToByteArray(this.value.toLowerCase().substring(2));
    }

    public static String ofZero() {
        return org.web3j.abi.datatypes.Address.DEFAULT.getValue();
    }
}
