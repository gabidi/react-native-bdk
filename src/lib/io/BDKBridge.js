import {NativeModules} from 'react-native';
const {BDKBridge: _bdk} = NativeModules;
const BDK = ({bdk = _bdk} = {}) => {
  const init = async (passphrase = '123') => {
    try {
      const {mnemonicWords, depositAddress} = await bdk.initConfig(passphrase);
      // await bdk.startBdk();
      return {mnemonicWords, depositAddress};
    } catch (err) {
      console.error('BDK:init', err);
      throw err;
    }
  };
  return {init};
};

export {BDK};
