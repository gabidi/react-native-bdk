/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useEffect, useState} from 'react';
import {
  SafeAreaView,
  StyleSheet,
  ScrollView,
  View,
  Text,
  StatusBar,
  TouchableOpacity,
} from 'react-native';

import {Header, Colors} from 'react-native/Libraries/NewAppScreen';
import {BDK} from './src/lib/io/BDKBridge';

const bdk = BDK();

const App: () => React$Node = () => {
  console.log(`V8 version is ${global._v8runtime().version}`);
  const [depositAddress, setDepositAddress] = useState('');
  const [mnemonicWords, setMnemonicWords] = useState('');
  useEffect(() => {
    initWallet();
  }, []);
  const initWallet = async () => {
    console.log('Calling init wallet');
    try {
      const {mnemonicWords: mw, depositAddress: da} = await bdk.init();
      setDepositAddress(da);
      setMnemonicWords(mw);
    } catch (err) {
      console.log(
        'Error init BDK, config could already be there trying to start',
      );
    }
    try {
      console.log('Calling start bdk');
      await bdk.startBdk();
    } catch (err) {
      console.error('error starting bdk', err);
    }
  };
  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ScrollView
          contentInsetAdjustmentBehavior="automatic"
          style={styles.scrollView}>
          <Header />
          <View style={styles.body}>
            <View style={styles.sectionContainer}>
              <Text style={styles.sectionTitle}>Step One</Text>
              {!!depositAddress ? (
                <>
                  <Text style={styles.sectionDescription}>BDK up !!</Text>
                  <Text style={styles.sectionDescription}>
                    {depositAddress}
                  </Text>
                </>
              ) : (
                <TouchableOpacity onPress={initWallet}>
                  <Text style={styles.sectionDescription}>
                    Press to init BDK
                  </Text>
                </TouchableOpacity>
              )}
            </View>
          </View>
        </ScrollView>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    backgroundColor: Colors.lighter,
  },
  engine: {
    position: 'absolute',
    right: 0,
  },
  body: {
    backgroundColor: Colors.white,
  },
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
    color: Colors.black,
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
    color: Colors.dark,
  },
  highlight: {
    fontWeight: '700',
  },
  footer: {
    color: Colors.dark,
    fontSize: 12,
    fontWeight: '600',
    padding: 4,
    paddingRight: 12,
    textAlign: 'right',
  },
});

export default App;
