import {NativeModules} from 'react-native';
const {BDKBridge: _bdk} = NativeModules;
const BDK = ({bdk = _bdk} = {}) => {
  const init = async (passphrase = '123123123') => {
    try {
      const {mnemonicWords, depositAddress} = await bdk.initConfig(passphrase);
      return {mnemonicWords, depositAddress};
    } catch (err) {
      console.error('BDK:init', err);
      throw err;
    }
  };
  const startBdk = async () => {
    try {
      await bdk.startBdk();
    } catch (err) {
      console.error('BDK:start', err);
      throw err;
    }
  };
  return {init, startBdk};
};

export {BDK};
