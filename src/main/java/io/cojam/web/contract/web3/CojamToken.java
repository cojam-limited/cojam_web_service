package io.cojam.web.contract.web3;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.16.
 */
@SuppressWarnings("rawtypes")
public class CojamToken extends Contract {
    public static final String BINARY = "0x60806040523480156200001157600080fd5b50600380546001600160a01b031916339081179091556040516000907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a36200006a336b1027e72f1f1281308800000062000071565b506200016a565b60006200008f826000546200012060201b620012071790919060201c565b60009081556001600160a01b038416815260016020908152604090912054620000c39184906200120762000120821b17901c565b6001600160a01b03841660008181526001602090815260408083209490945583518681529351929391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a350600192915050565b60008282018381101562000163576040805162461bcd60e51b81526020600482015260056024820152640a69a7464760db1b604482015290519081900360640190fd5b9392505050565b611c54806200017a6000396000f3fe608060405234801561001057600080fd5b506004361061018e5760003560e01c80638456cb59116100de578063c4f3a85311610097578063dd62ed3e11610071578063dd62ed3e146104fe578063e58398361461052c578063f2fde38b14610552578063f7b188a5146105785761018e565b8063c4f3a8531461048c578063d1c46916146104b2578063d8fb9337146104d85761018e565b80638456cb59146103c15780638d1fdf2f146103c95780638da5cb5b146103ef57806395d89b4114610413578063a9059cbb1461041b578063b2520a7c146104475761018e565b806342966c681161014b578063715018a611610125578063715018a61461033b57806379cc6790146103435780637eee288d1461036f57806383cfab421461039b5761018e565b806342966c68146102f05780635c975abb1461030d57806370a08231146103155761018e565b806306fdde0314610193578063095ea7b31461021057806318160ddd1461025057806323b872dd1461026a578063313ce567146102a057806338b82092146102be575b600080fd5b61019b610580565b6040805160208082528351818301528351919283929083019185019080838360005b838110156101d55781810151838201526020016101bd565b50505050905090810190601f1680156102025780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b61023c6004803603604081101561022657600080fd5b506001600160a01b03813516906020013561059f565b604080519115158252519081900360200190f35b6102586105fb565b60408051918252519081900360200190f35b61023c6004803603606081101561028057600080fd5b506001600160a01b03813581169160208101359091169060400135610601565b6102a86107e1565b6040805160ff9092168252519081900360200190f35b61023c600480360360608110156102d457600080fd5b506001600160a01b0381351690602081013590604001356107e6565b61023c6004803603602081101561030657600080fd5b5035610899565b61023c610928565b6102586004803603602081101561032b57600080fd5b50356001600160a01b0316610931565b61023c61094c565b61023c6004803603604081101561035957600080fd5b506001600160a01b0381351690602001356109a7565b61023c6004803603604081101561038557600080fd5b506001600160a01b038135169060200135610a8a565b61023c600480360360208110156103b157600080fd5b50356001600160a01b0316610b10565b61023c610baa565b61023c600480360360208110156103df57600080fd5b50356001600160a01b0316610c74565b6103f7610d11565b604080516001600160a01b039092168252519081900360200190f35b61019b610d20565b61023c6004803603604081101561043157600080fd5b506001600160a01b038135169060200135610d3c565b6104736004803603604081101561045d57600080fd5b506001600160a01b038135169060200135610eb5565b6040805192835260208301919091528051918290030190f35b61023c600480360360208110156104a257600080fd5b50356001600160a01b0316610f1d565b61023c600480360360208110156104c857600080fd5b50356001600160a01b0316610fa5565b610473600480360360208110156104ee57600080fd5b50356001600160a01b0316611030565b6102586004803603604081101561051457600080fd5b506001600160a01b0381358116916020013516611058565b61023c6004803603602081101561054257600080fd5b50356001600160a01b0316611083565b61023c6004803603602081101561056857600080fd5b50356001600160a01b03166110a1565b61023c611141565b604080518082019091526005815264436f6a616d60d81b602082015290565b60006001600160a01b0383166105e65760405162461bcd60e51b815260040180806020018281038252602c815260200180611a74602c913960400191505060405180910390fd5b6105f1338484611249565b5060019392505050565b60005490565b6001600160a01b038316600090815260076020526040812054849060ff1615610671576040805162461bcd60e51b815260206004820152601c60248201527f467265657a61626c65203a207461726765742069732066726f7a656e00000000604482015290519081900360640190fd5b60065460ff16156106b35760405162461bcd60e51b815260040180806020018281038252603981526020018061192f6039913960400191505060405180910390fd5b6001600160a01b038516600090815260056020526040902054859084906106da9082611207565b6001600160a01b03831660009081526001602052604090205410156107305760405162461bcd60e51b8152600401808060200182810382526032815260200180611a0d6032913960400191505060405180910390fd5b6001600160a01b0386166107755760405162461bcd60e51b81526004018080602001828103825260318152602001806118fe6031913960400191505060405180910390fd5b6107808787876112b4565b506107d387336107ce8860405180606001604052806031815260200161199f603191396001600160a01b038d1660009081526002602090815260408083203384529091529020549190611383565b611249565b506001979650505050505050565b601290565b6003546000906001600160a01b031633146108325760405162461bcd60e51b815260040180806020018281038252602f815260200180611aa0602f913960400191505060405180910390fd5b6001600160a01b0384166108775760405162461bcd60e51b815260040180806020018281038252603d8152602001806119d0603d913960400191505060405180910390fd5b6108823385856112b4565b5061088e84848461141a565b506001949350505050565b60065460009060ff16156108de5760405162461bcd60e51b815260040180806020018281038252603981526020018061192f6039913960400191505060405180910390fd5b6108e83383611593565b60408051848152905191925033917fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca59181900360200190a2506001919050565b60065460ff1690565b6001600160a01b031660009081526001602052604090205490565b6003546000906001600160a01b031633146109985760405162461bcd60e51b815260040180806020018281038252602f815260200180611aa0602f913960400191505060405180910390fd5b6109a2600061166f565b905090565b60065460009060ff16156109ec5760405162461bcd60e51b815260040180806020018281038252603981526020018061192f6039913960400191505060405180910390fd5b6109f68383611593565b506040805183815290516001600160a01b038516917fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5919081900360200190a2610a8383336107ce85604051806060016040528060378152602001611968603791396001600160a01b03891660009081526002602090815260408083203384529091529020549190611383565b9392505050565b6001600160a01b0382166000908152600460205260408120805442919084908110610ab157fe5b90600052602060002090600202016001015410610aff5760405162461bcd60e51b815260040180806020018281038252602d815260200180611b78602d913960400191505060405180910390fd5b610b0983836116d2565b5092915050565b6003546000906001600160a01b03163314610b5c5760405162461bcd60e51b815260040180806020018281038252602f815260200180611aa0602f913960400191505060405180910390fd5b6001600160a01b038216600081815260076020526040808220805460ff19169055517fca5069937e68fd197927055037f59d7c90bf75ac104e6e375539ef480c3ad6ee9190a2506001919050565b6003546000906001600160a01b03163314610bf65760405162461bcd60e51b815260040180806020018281038252602f815260200180611aa0602f913960400191505060405180910390fd5b60065460ff1615610c385760405162461bcd60e51b815260040180806020018281038252603981526020018061192f6039913960400191505060405180910390fd5b6006805460ff191660011790556040517f9e87fac88ff661f02d44f95383c817fece4bce600a3dab7a54406878b965e75290600090a150600190565b6003546000906001600160a01b03163314610cc05760405162461bcd60e51b815260040180806020018281038252602f815260200180611aa0602f913960400191505060405180910390fd5b6001600160a01b038216600081815260076020526040808220805460ff19166001179055517faf85b60d26151edd11443b704d424da6c43d0468f2235ebae3d1904dbc3230499190a2506001919050565b6003546001600160a01b031690565b60408051808201909152600281526110d560f21b602082015290565b3360008181526007602052604081205490919060ff1615610da4576040805162461bcd60e51b815260206004820152601c60248201527f467265657a61626c65203a207461726765742069732066726f7a656e00000000604482015290519081900360640190fd5b60065460ff1615610de65760405162461bcd60e51b815260040180806020018281038252603981526020018061192f6039913960400191505060405180910390fd5b336000818152600560205260409020548490610e029082611207565b6001600160a01b0383166000908152600160205260409020541015610e585760405162461bcd60e51b8152600401808060200182810382526032815260200180611a0d6032913960400191505060405180910390fd5b6001600160a01b038616610e9d5760405162461bcd60e51b815260040180806020018281038252602d815260200180611b0c602d913960400191505060405180910390fd5b610ea83387876112b4565b5060019695505050505050565b600080610ec0611881565b6001600160a01b0385166000908152600460205260409020805485908110610ee457fe5b60009182526020918290206040805180820190915260029092020180548083526001909101549190920181905290969095509350505050565b6000805b6001600160a01b038316600090815260046020526040902054811015610f9c576001600160a01b0383166000908152600460205260409020805442919083908110610f6857fe5b9060005260206000209060020201600101541015610f9457610f8a83826116d2565b15610f9457600019015b600101610f21565b50600192915050565b6003546000906001600160a01b03163314610ff15760405162461bcd60e51b815260040180806020018281038252602f815260200180611aa0602f913960400191505060405180910390fd5b60005b6001600160a01b038316600090815260046020526040902054811015610f9c5761101e83826116d2565b1561102857600019015b600101610ff4565b6001600160a01b03166000908152600560209081526040808320546004909252909120549091565b6001600160a01b03918216600090815260026020908152604080832093909416825291909152205490565b6001600160a01b031660009081526007602052604090205460ff1690565b6003546000906001600160a01b031633146110ed5760405162461bcd60e51b815260040180806020018281038252602f815260200180611aa0602f913960400191505060405180910390fd5b6001600160a01b0382166111325760405162461bcd60e51b8152600401808060200182810382526045815260200180611bda6045913960600191505060405180910390fd5b61113b8261166f565b92915050565b6003546000906001600160a01b0316331461118d5760405162461bcd60e51b815260040180806020018281038252602f815260200180611aa0602f913960400191505060405180910390fd5b60065460ff166111ce5760405162461bcd60e51b8152600401808060200182810382526035815260200180611ba56035913960400191505060405180910390fd5b6006805460ff191690556040517fa45f47fdea8a1efdd9029a5691c7f759c32b7c698632b563573e155625d1693390600090a150600190565b600082820183811015610a83576040805162461bcd60e51b81526020600482015260056024820152640a69a7464760db1b604482015290519081900360640190fd5b6001600160a01b03808416600081815260026020908152604080832094871680845294825280832086905580518681529051929493927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929181900390910190a35060019392505050565b60006112f3826040518060600160405280603d8152602001611acf603d91396001600160a01b0387166000908152600160205260409020549190611383565b6001600160a01b0380861660009081526001602052604080822093909355908516815220546113229083611207565b6001600160a01b0380851660008181526001602090815260409182902094909455805186815290519193928816927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a35060019392505050565b600081848411156114125760405162461bcd60e51b81526004018080602001828103825283818151815260200191508051906020019080838360005b838110156113d75781810151838201526020016113bf565b50505050905090810190601f1680156114045780820380516001836020036101000a031916815260200191505b509250505060405180910390fd5b505050900390565b600042821161145a5760405162461bcd60e51b815260040180806020018281038252602a81526020018061189c602a913960400191505060405180910390fd5b6001600160a01b03841660009081526005602052604090205461147e908490611207565b6001600160a01b03851660009081526001602052604090205410156114d45760405162461bcd60e51b815260040180806020018281038252603f815260200180611b39603f913960400191505060405180910390fd5b6001600160a01b0384166000908152600560205260409020546114f79084611207565b6001600160a01b03851660008181526005602090815260408083209490945560048152838220845180860186528881528083018881528254600181810185559386529484902091516002909502909101938455519201919091558251868152908101859052825191927f49eaf4942f1237055eb4cfa5f31c9dfe50d5b4ade01e021f7de8be2fbbde557b92918290030190a25060019392505050565b60006115d2826040518060600160405280603881526020016118c6603891396001600160a01b0386166000908152600160205260409020549190611383565b60016000856001600160a01b03166001600160a01b031681526020019081526020016000208190555061162282604051806060016040528060358152602001611a3f603591396000549190611383565b60009081556040805184815290516001600160a01b038616917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef919081900360200190a350600192915050565b6003546040516000916001600160a01b03808516929116907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908490a350600380546001600160a01b0319166001600160a01b0392909216919091179055600190565b6001600160a01b03821660009081526004602052604081208054829190849081106116f957fe5b60009182526020808320600290920290910180546001600160a01b038816845260059092526040909220549192506117319190611857565b6001600160a01b0385166000818152600560209081526040918290209390935583548151908152905191927f6381d9813cabeb57471b5a7e05078e64845ccdb563146a6911d536f24ce960f192918290030190a26001600160a01b0384166000908152600460205260409020805460001981019081106117ad57fe5b906000526020600020906002020160046000866001600160a01b03166001600160a01b0316815260200190815260200160002084815481106117eb57fe5b60009182526020808320845460029093020191825560019384015493909101929092556001600160a01b038616815260049091526040902080548061182c57fe5b6000828152602081206002600019909301928302018181556001908101919091559155949350505050565b6000610a83838360405180604001604052806005815260200164534d3a343360d81b815250611383565b60405180604001604052806000815260200160008152509056fe4b4950374c6f636b61626c652f6c6f636b203a2043616e6e6f74207365742064756520746f20706173744b4950374275726e61626c652f6275726e203a2043616e6e6f74206275726e206d6f7265207468616e207573657227732062616c616e636543542f7472616e7366657246726f6d203a2053686f756c64206e6f742073656e6420746f207a65726f2061646472657373506175736564203a20546869732066756e6374696f6e2063616e206f6e6c792062652063616c6c6564207768656e206e6f74207061757365644b4950374275726e61626c652f6275726e46726f6d203a2043616e6e6f74206275726e206d6f7265207468616e20616c6c6f77616e636543542f7472616e7366657246726f6d203a2043616e6e6f742073656e64206d6f7265207468616e20616c6c6f77616e63654b4950374c6f636b61626c652f7472616e73666572576974684c6f636b5570203a2043616e6e6f742073656e6420746f207a65726f20616464726573734b4950374c6f636b61626c652f43616e6e6f742073656e64206d6f7265207468616e20756e6c6f636b656420616d6f756e744b4950374275726e61626c652f6275726e203a2043616e6e6f74206275726e206d6f7265207468616e20746f74616c537570706c7943542f617070726f7665203a2053686f756c64206e6f7420617070726f7665207a65726f20616464726573734f776e61626c65203a2046756e6374696f6e2063616c6c656420627920756e617574686f72697a656420757365722e4b4950372f7472616e73666572203a2063616e6e6f74207472616e73666572206d6f7265207468616e20746f6b656e206f776e65722062616c616e636543542f7472616e73666572203a2053686f756c64206e6f742073656e6420746f207a65726f20616464726573734b4950374c6f636b61626c652f6c6f636b203a206c6f636b656420746f74616c2073686f756c6420626520736d616c6c6572207468616e2062616c616e63654b4950374c6f636b61626c652f756e6c6f636b3a2063616e6e6f7420756e6c6f636b206265666f726520647565506175736564203a20546869732066756e6374696f6e2063616e206f6e6c792062652063616c6c6564207768656e207061757365644f776e61626c652f7472616e736665724f776e657273686970203a2063616e6e6f74207472616e73666572206f776e65727368697020746f207a65726f2061646472657373a264697066735822122090190b788a7fa092170e587cf063424a43879056fcd28c9069099e801087b0ac64736f6c63430007010033";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_BURNFROM = "burnFrom";

    public static final String FUNC_FREEZE = "freeze";

    public static final String FUNC_ISFROZEN = "isFrozen";

    public static final String FUNC_LOCKINFO = "lockInfo";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PAUSE = "pause";

    public static final String FUNC_PAUSED = "paused";

    public static final String FUNC_RELEASELOCK = "releaseLock";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_TOTALLOCKED = "totalLocked";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_TRANSFERWITHLOCKUP = "transferWithLockUp";

    public static final String FUNC_UNFREEZE = "unFreeze";

    public static final String FUNC_UNPAUSE = "unPause";

    public static final String FUNC_UNLOCK = "unlock";

    public static final String FUNC_UNLOCKALL = "unlockAll";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_DECIMALS = "decimals";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event BURN_EVENT = new Event("Burn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event FREEZE_EVENT = new Event("Freeze", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event LOCK_EVENT = new Event("Lock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event PAUSED_EVENT = new Event("Paused", 
            Arrays.<TypeReference<?>>asList());
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event UNFREEZE_EVENT = new Event("Unfreeze", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event UNLOCK_EVENT = new Event("Unlock", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event UNPAUSED_EVENT = new Event("Unpaused", 
            Arrays.<TypeReference<?>>asList());
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("8217", "0x7F223b1607171B81eBd68D22f1Ca79157Fd4A44b");
    }

    @Deprecated
    protected CojamToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CojamToken(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CojamToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CojamToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(BURN_EVENT, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.burned = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<BurnEventResponse> burnEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BURN_EVENT, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.log = log;
                typedResponse.burned = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<BurnEventResponse> burnEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURN_EVENT));
        return burnEventFlowable(filter);
    }

    public List<FreezeEventResponse> getFreezeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(FREEZE_EVENT, transactionReceipt);
        ArrayList<FreezeEventResponse> responses = new ArrayList<FreezeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            FreezeEventResponse typedResponse = new FreezeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.target = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<FreezeEventResponse> freezeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, FreezeEventResponse>() {
            @Override
            public FreezeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(FREEZE_EVENT, log);
                FreezeEventResponse typedResponse = new FreezeEventResponse();
                typedResponse.log = log;
                typedResponse.target = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<FreezeEventResponse> freezeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(FREEZE_EVENT));
        return freezeEventFlowable(filter);
    }

    public List<LockEventResponse> getLockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(LOCK_EVENT, transactionReceipt);
        ArrayList<LockEventResponse> responses = new ArrayList<LockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LockEventResponse typedResponse = new LockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.due = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LockEventResponse> lockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LockEventResponse>() {
            @Override
            public LockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOCK_EVENT, log);
                LockEventResponse typedResponse = new LockEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.due = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LockEventResponse> lockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOCK_EVENT));
        return lockEventFlowable(filter);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.currentOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.currentOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public List<PausedEventResponse> getPausedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PAUSED_EVENT, transactionReceipt);
        ArrayList<PausedEventResponse> responses = new ArrayList<PausedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PausedEventResponse typedResponse = new PausedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<PausedEventResponse> pausedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, PausedEventResponse>() {
            @Override
            public PausedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PAUSED_EVENT, log);
                PausedEventResponse typedResponse = new PausedEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Flowable<PausedEventResponse> pausedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PAUSED_EVENT));
        return pausedEventFlowable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public List<UnfreezeEventResponse> getUnfreezeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UNFREEZE_EVENT, transactionReceipt);
        ArrayList<UnfreezeEventResponse> responses = new ArrayList<UnfreezeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UnfreezeEventResponse typedResponse = new UnfreezeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.target = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<UnfreezeEventResponse> unfreezeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, UnfreezeEventResponse>() {
            @Override
            public UnfreezeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UNFREEZE_EVENT, log);
                UnfreezeEventResponse typedResponse = new UnfreezeEventResponse();
                typedResponse.log = log;
                typedResponse.target = (String) eventValues.getIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<UnfreezeEventResponse> unfreezeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNFREEZE_EVENT));
        return unfreezeEventFlowable(filter);
    }

    public List<UnlockEventResponse> getUnlockEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UNLOCK_EVENT, transactionReceipt);
        ArrayList<UnlockEventResponse> responses = new ArrayList<UnlockEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UnlockEventResponse typedResponse = new UnlockEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<UnlockEventResponse> unlockEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, UnlockEventResponse>() {
            @Override
            public UnlockEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UNLOCK_EVENT, log);
                UnlockEventResponse typedResponse = new UnlockEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<UnlockEventResponse> unlockEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNLOCK_EVENT));
        return unlockEventFlowable(filter);
    }

    public List<UnpausedEventResponse> getUnpausedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(UNPAUSED_EVENT, transactionReceipt);
        ArrayList<UnpausedEventResponse> responses = new ArrayList<UnpausedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UnpausedEventResponse typedResponse = new UnpausedEventResponse();
            typedResponse.log = eventValues.getLog();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<UnpausedEventResponse> unpausedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, UnpausedEventResponse>() {
            @Override
            public UnpausedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(UNPAUSED_EVENT, log);
                UnpausedEventResponse typedResponse = new UnpausedEventResponse();
                typedResponse.log = log;
                return typedResponse;
            }
        });
    }

    public Flowable<UnpausedEventResponse> unpausedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UNPAUSED_EVENT));
        return unpausedEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> allowance(String owner, String spender) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner), 
                new org.web3j.abi.datatypes.Address(spender)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String owner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(owner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> burn(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> burnFrom(String burned, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURNFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(burned), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> freeze(String target) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_FREEZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(target)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> isFrozen(String target) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISFROZEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(target)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> lockInfo(String locked, BigInteger index) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_LOCKINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(locked), 
                new org.web3j.abi.datatypes.generated.Uint256(index)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> pause() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PAUSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> paused() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PAUSED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> releaseLock(String from) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RELEASELOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(from)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> totalLocked(String locked) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALLOCKED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(locked)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFEROWNERSHIP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferWithLockUp(String recipient, BigInteger amount, BigInteger due) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERWITHLOCKUP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(recipient), 
                new org.web3j.abi.datatypes.generated.Uint256(amount), 
                new org.web3j.abi.datatypes.generated.Uint256(due)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unFreeze(String target) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UNFREEZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(target)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unPause() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UNPAUSE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unlock(String from, BigInteger idx) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UNLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(from), 
                new org.web3j.abi.datatypes.generated.Uint256(idx)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> unlockAll(String from) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_UNLOCKALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(from)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String to, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String from, String to, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(from), 
                new org.web3j.abi.datatypes.Address(to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> name() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> symbol() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static CojamToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CojamToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CojamToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CojamToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CojamToken load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CojamToken(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CojamToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CojamToken(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CojamToken> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CojamToken.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<CojamToken> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CojamToken.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CojamToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CojamToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CojamToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CojamToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger amount;
    }

    public static class BurnEventResponse extends BaseEventResponse {
        public String burned;

        public BigInteger amount;
    }

    public static class FreezeEventResponse extends BaseEventResponse {
        public String target;
    }

    public static class LockEventResponse extends BaseEventResponse {
        public String from;

        public BigInteger amount;

        public BigInteger due;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String currentOwner;

        public String newOwner;
    }

    public static class PausedEventResponse extends BaseEventResponse {
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger amount;
    }

    public static class UnfreezeEventResponse extends BaseEventResponse {
        public String target;
    }

    public static class UnlockEventResponse extends BaseEventResponse {
        public String from;

        public BigInteger amount;
    }

    public static class UnpausedEventResponse extends BaseEventResponse {
    }
}
