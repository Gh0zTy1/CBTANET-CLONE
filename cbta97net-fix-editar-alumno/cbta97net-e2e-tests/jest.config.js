export default {
  testEnvironment: 'node',
  testTimeout: 30000,
  transform: {},

  reporters: [
    "default",
    [
      "jest-html-reporters",
      {
        publicPath: "./reports",
        filename: "jest-report.html",
        expand: true
      }
    ]
  ]
};